package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.DatastoreService;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.service.store.*;
import org.activityinfo.service.tables.StoreAccessor;
import org.activityinfo.store.hrd.cache.WorkspaceCache;
import org.activityinfo.store.hrd.dao.*;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.index.WorkspaceIndex;
import org.activityinfo.store.hrd.tx.ReadTx;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

public class HrdResourceStore implements ResourceStore {
    private final StoreContext context;
    private final DatastoreService datastore;
    private final WorkspaceCache workspaceLookup;

    @Inject
    public HrdResourceStore(StoreContext store) {
        this.context = store;
        this.datastore = store.getDatastore();
        this.workspaceLookup = store.getWorkspaceCache();
    }

    private WorkspaceUpdate beginUpdate(AuthenticatedUser user, ResourceId id) {
        WorkspaceEntityGroup workspace = workspaceLookup.lookup(id);
        return WorkspaceUpdate.newBuilder(context, workspace, user).begin();
    }

    private WorkspaceQuery beginQuery(AuthenticatedUser user, ResourceId resourceId) {
        WorkspaceEntityGroup workspace = workspaceLookup.lookup(resourceId);
        ReadTx tx = ReadTx.withSerializableConsistency(datastore);
        return new WorkspaceQuery(context, workspace, user, tx);
    }

    @Override
    public UserResource get(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId) {
        try (WorkspaceQuery query = beginQuery(user, resourceId)) {
            return query.getResource(resourceId).asUserResource();
        }
    }

    public StoreContext getContext() {
        return context;
    }

    @Override
    public List<Resource> getAccessControlRules(@InjectParam AuthenticatedUser user,
                                                @PathParam("id") ResourceId resourceId) {

        try (WorkspaceQuery query = beginQuery(user, resourceId)) {
            return query.getResource(resourceId).getAccessControlRules();
        }
    }

    /**
     * Deletes {@code Resource} from the store
     *
     * @param user       authenticated user
     * @param resourceId resource id
     * @return result whether resource was deleted or not
     */
    @DELETE
    @Path("resource/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public UpdateResult delete(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId) {

        try (WorkspaceUpdate update = beginUpdate(user, resourceId)) {
            update.delete(resourceId);
            update.commit();

            return UpdateResult.committed(resourceId, update.getUpdateVersion());
        }
    }

    @Override
    public UpdateResult put(AuthenticatedUser user, Resource resource) {

        try (WorkspaceUpdate update = beginUpdate(user, resource.getId())) {

            update.updateResource(resource);
            update.commit();

            return UpdateResult.committed(resource.getId(), update.getUpdateVersion());
        }
    }

    @Override
    public UpdateResult create(AuthenticatedUser user, Resource resource) {
        if (user.isAnonymous()) {
            throw new WebApplicationException(UNAUTHORIZED);
        }

        try {
            if (resource.getOwnerId().equals(Resources.ROOT_ID)) {
                return createWorkspace(user, resource);
            } else {
                return createResource(user, resource);
            }

        } catch(ResourceExistsException e) {
            return UpdateResult.committed(e.getResourceId(), e.getVersion());
        }
    }


    private UpdateResult createWorkspace(AuthenticatedUser user, Resource resource) {
        WorkspaceCreation creation = new WorkspaceCreation(context, user);
        creation.createWorkspace(resource);

        return UpdateResult.committed(resource.getId(), WorkspaceCreation.INITIAL_VERSION);
    }

    private UpdateResult createResource(AuthenticatedUser user, Resource resource) {
        try(WorkspaceUpdate update = beginUpdate(user, resource.getOwnerId())) {
            update.createResource(resource);
            update.commit();

            // pre-cache this resource's workspace so the user can find it right after the call
            // but possible before there was time to update the index

            context.getWorkspaceCache().cache(resource.getId(), update.getWorkspace());

            return UpdateResult.committed(resource.getId(), update.getUpdateVersion());
        }
    }


    @Override
    public FolderProjection queryTree(@InjectParam AuthenticatedUser user,
                                      FolderRequest request) {

        try(WorkspaceQuery query = beginQuery(user, request.getRootId())) {

            ResourceQuery resource = query.getResource(request.getRootId());
            ResourceNode rootNode = resource.asResourceNode();
            for(ResourceNode child : resource.getFolderItems()) {
                rootNode.getChildren().add(child);
            }

            return new FolderProjection(rootNode);
        }
    }


    @POST
    @Path("query/cube")
    @Consumes("application/json")
    @Produces("application/json")
    public List<Bucket> queryCube(@InjectParam AuthenticatedUser user, PivotTableModel tableModel) {
        throw new UnsupportedOperationException();
    }


    @Override
    public List<ResourceNode> getOwnedOrSharedWorkspaces(@InjectParam AuthenticatedUser user) {
        return WorkspaceIndex.queryUserWorkspaces(user);
    }

    @Override
    public List<Resource> getUpdates(@InjectParam AuthenticatedUser user, ResourceId workspaceId, long version) {
        return new SyncBuilder(workspaceId, user).getUpdates(version);
    }

    @Override
    public StoreLoader beginLoad(AuthenticatedUser user, ResourceId parentId) {
        return BulkLoader.newBuilder(context)
            .setUser(user)
            .setParentId(parentId)
            .begin();
    }

    @Override
    public StoreReader openReader(AuthenticatedUser user) {
        return new HrdStoreReader(context, user);
    }

    public StoreAccessor createAccessor(AuthenticatedUser user) {
        return null;
    }
}
