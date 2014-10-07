package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.apphosting.api.ApiProxy;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.cubes.CubeBuilder;
import org.activityinfo.service.store.FolderRequest;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.StoreLoader;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tables.TableBuilder;
import org.activityinfo.store.hrd.cache.WorkspaceCache;
import org.activityinfo.store.hrd.dao.*;
import org.activityinfo.store.hrd.entity.workspace.*;
import org.activityinfo.store.hrd.index.WorkspaceIndex;
import org.activityinfo.store.hrd.tx.ReadTx;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

public class HrdResourceStore implements ResourceStore {
    private final static long TIME_LIMIT_MILLISECONDS = 10 * 1000L;

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
        WorkspaceQuery query = new WorkspaceQuery(context, workspace, user, tx);
        return query;
    }

    @Override
    public UserResource get(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId) {

        try (WorkspaceQuery query = beginQuery(user, resourceId)) {
            return query.getResource(resourceId).asUserResource();
        }
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
            for(ResourceNode child : resource.queryFolderItems()) {
                rootNode.getChildren().add(child);
            }

            return new FolderProjection(rootNode);
        }
    }

    @Override
    public TableData queryTable(@InjectParam AuthenticatedUser user, TableModel tableModel) {
        TableBuilder builder = new TableBuilder(new HrdStoreAccessor(context, user));
        try {
            return builder.buildTable(tableModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HrdStoreAccessor createAccessor(AuthenticatedUser user) {
        return new HrdStoreAccessor(context, user);
    }

    @POST
    @Path("query/cube")
    @Consumes("application/json")
    @Produces("application/json")
    public List<Bucket> queryCube(@InjectParam AuthenticatedUser user, PivotTableModel tableModel) {
        CubeBuilder builder = new CubeBuilder(new HrdStoreAccessor(context, user));
        try {
            return builder.buildCube(tableModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<ResourceNode> getOwnedOrSharedWorkspaces(@InjectParam AuthenticatedUser user) {
        return WorkspaceIndex.queryUserWorkspaces(user);
    }

    @Override
    public List<Resource> getUpdates(@InjectParam AuthenticatedUser user, ResourceId workspaceId, long version) {

        ApiProxy.Environment environment = ApiProxy.getCurrentEnvironment();
        Map<ResourceId, SnapshotKey> snapshots = Maps.newLinkedHashMap();
        Map<ResourceId, Authorization> authorizations = Maps.newHashMap();
        WorkspaceEntityGroup workspace = new WorkspaceEntityGroup(workspaceId);

        if (version < 0) version = 0;

        try (ReadTx tx = ReadTx.withSerializableConsistency(datastore)) {

            for (SnapshotKey snapshot : tx.query(Snapshot.afterVersion(workspace, version))) {
                ResourceId resourceId = snapshot.getResourceId();

                // We want the linked list to be sorted based on the most recent insertion of a resource
                snapshots.remove(resourceId);
                snapshots.put(resourceId, snapshot);

                if (authorizations.get(resourceId) == null) {
                    authorizations.put(resourceId, new Authorization(workspace, user, resourceId, tx));
                }

                if (environment.getRemainingMillis() < TIME_LIMIT_MILLISECONDS) {
                    break;
                }
            }

            List<Resource> resources = Lists.newArrayListWithCapacity(snapshots.size());

            for (SnapshotKey snapshot : snapshots.values()) {
                final Authorization authorization;
                Resource resource = tx.getOrThrow(snapshot).toResource();

                if (AccessControlRule.CLASS_ID.equals(resource.getValue().getClassId())) {
                    final Optional<Authorization> oldAuthorization;
                    final Optional<Snapshot> optionalSnapshot = tx.query(Snapshot.asOf(workspace, resource.getId(), version));

                    authorization = new Authorization(user, resource);

                    if (optionalSnapshot.isPresent()) {
                        oldAuthorization = Optional.of(new Authorization(user, optionalSnapshot.get().toResource()));
                    } else {
                        oldAuthorization = Optional.absent();
                    }

                    // TODO Deal with the effects of changed authorizations correctly
                    for (Resource newlyAuthorizedResource : applyAuthorization(null, workspace, oldAuthorization, authorization, tx)) {
                        if (!snapshots.keySet().contains(newlyAuthorizedResource.getId())) {
                            resources.add(newlyAuthorizedResource);
                        }
                    }
                } else {
                    authorization = authorizations.get(resource.getId());
                }

                if (authorization.canView()) resources.add(resource);
            }

            return resources;
        }
    }

    @Override
    public StoreLoader beginLoad(AuthenticatedUser user, ResourceId parentId) {
        return BulkLoader.newBuilder(context)
            .setUser(user)
            .setParentId(parentId)
            .begin();
    }

    private static Collection<Resource> applyAuthorization(AuthenticatedUser authenticatedUser, WorkspaceEntityGroup workspace,
                                                           Optional<Authorization> oldAuthorization,
                                                           Authorization newAuthorization, ReadTx tx) {
        if (newAuthorization.canViewNowButNotAsOf(oldAuthorization)) {
            ResourceId resourceId = newAuthorization.getResourceId();

            if (resourceId != null) {
                final Collection<Resource> result = Lists.newArrayList();

                LatestVersionKey parentKey = new LatestVersionKey(workspace, resourceId);

                for (LatestVersionKey element : descendIdTree(authenticatedUser, parentKey, newAuthorization, tx)) {
                    result.add(tx.getOrThrow(element).toResource());
                }

                return result;
            }
        }

        return Collections.emptySet();
    }

    // This method recursively descends the ID tree of a resource node, including only nodes with the same authorization
    private static List<LatestVersionKey> descendIdTree(AuthenticatedUser authenticatedUser, LatestVersionKey parentKey, Authorization parentAuth, ReadTx tx) {
        final List<LatestVersionKey> result = Lists.newArrayList(parentKey);

        if (parentAuth != null && parentKey.getResourceId() != null && tx != null) {
            ResourceId authorizationId = parentAuth.getId();

            if (authorizationId != null) {
                for (LatestVersionKey child : tx.query(LatestVersion.queryChildKeys(parentKey))) {
                    Authorization childAuth = new Authorization(parentKey.getWorkspace(), authenticatedUser, child.getResourceId(), tx);
                    if (authorizationId.equals(childAuth.getId())) {
                        result.addAll(descendIdTree(authenticatedUser, child, parentAuth, tx));
                    }
                }
            }
        }
        return result;
    }

}
