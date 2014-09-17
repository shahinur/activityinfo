package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.*;
import com.google.apphosting.api.ApiProxy;
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
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tables.TableBuilder;
import org.activityinfo.store.hrd.entity.*;
import org.activityinfo.store.hrd.index.AcrIndex;
import org.activityinfo.store.hrd.index.WorkspaceIndex;
import org.activityinfo.store.hrd.index.WorkspaceLookup;

import javax.ws.rs.*;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

public class HrdResourceStore implements ResourceStore {
    private final static long TIME_LIMIT_MILLISECONDS = 10 * 1000L;


    private final DatastoreService datastore;
    private final ClientIdProvider clientIdProvider = new ClientIdProvider();
    private final WorkspaceLookup workspaceLookup = new WorkspaceLookup();

    public HrdResourceStore() {
        this.datastore = DatastoreServiceFactory.getDatastoreService(DatastoreServiceConfig.Builder
            .withImplicitTransactionManagementPolicy(ImplicitTransactionManagementPolicy.NONE));
    }

    @Override
    public long generateClientId(AuthenticatedUser user) {
        return clientIdProvider.getNext();
    }


    private WorkspaceTransaction begin(Workspace workspace, AuthenticatedUser user) {
        return new UpdateTransaction(workspace, datastore, user);
    }


    private WorkspaceTransaction beginRead(Workspace workspace, AuthenticatedUser user) {
        return new ReadTransaction(workspace, datastore, user);
    }


    @GET
    @Path("resource/{id}")
    @Produces("application/json")
    @Override
    public Resource get(AuthenticatedUser user, @PathParam("id") ResourceId resourceId) {
        try {
            Workspace workspace = workspaceLookup.lookup(resourceId);

            try(WorkspaceTransaction tx = beginRead(workspace, user)) {
                Authorization authorization = new Authorization(user, resourceId, tx);

                if (authorization.canView()) {
                    return workspace.getLatestContent(resourceId).get(tx);
                } else {
                    throw new WebApplicationException(UNAUTHORIZED);
                }
            }
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound(resourceId);
        }
    }



    @Override
    public List<Resource> getAccessControlRules(@InjectParam AuthenticatedUser user,
                                                @PathParam("id") ResourceId resourceId) {

         return Lists.newArrayList(AcrIndex.queryRules(datastore, resourceId));
    }

    @Override
    public UpdateResult put(@InjectParam AuthenticatedUser user,
                            @PathParam("id") ResourceId resourceId,
                            Resource resource) {

       return put(user, resource);
    }


    @Override
    public UpdateResult put(AuthenticatedUser user, Resource resource) {
        Workspace workspace = workspaceLookup.lookup(resource.getId());

        try (WorkspaceTransaction tx = begin(workspace, user)) {
            Authorization authorization = new Authorization(user, resource.getId(), tx);

            if (authorization.canEdit()) {
                long newVersion = workspace.createResource(tx, resource);
                tx.commit();

                return UpdateResult.committed(resource.getId(), newVersion);
            } else {
                return create(tx, user, resource);
            }
        }
    }

    @Override
    public UpdateResult create(AuthenticatedUser user, Resource resource) {
        if(resource.getOwnerId().equals(Resources.ROOT_ID)) {
            Workspace workspace = new Workspace(resource.getId());
            try(WorkspaceTransaction tx = begin(workspace, user)) {
                long newVersion = workspace.createWorkspace(tx, resource);
                tx.commit();
                return UpdateResult.committed(resource.getId(), newVersion);
            }

        } else {
            Workspace workspace = workspaceLookup.lookup(resource.getOwnerId());

            try (WorkspaceTransaction tx = begin(workspace, user)) {
                return create(tx, user, resource);
            }
        }
    }

    private UpdateResult create(WorkspaceTransaction tx, AuthenticatedUser user, Resource resource) {
        Authorization authorization = new Authorization(user, resource.getOwnerId(), tx);
        Workspace workspace = tx.getWorkspace();

        if (authorization.canEdit()) {
            try {
                workspace.getLatestContent(resource.getId()).get(tx);
                return UpdateResult.rejected();
            } catch (EntityNotFoundException e) {
                long newVersion = workspace.createResource(tx, resource);
                tx.commit();

                // Cache immediately so that subsequent reads will be able to find the resource
                // if it takes a while for the indices to catch up
                workspaceLookup.cache(resource.getId(), workspace);

                return UpdateResult.committed(resource.getId(), newVersion);
            }
        } else {
            throw new WebApplicationException(UNAUTHORIZED);
        }
    }


    @Override
    public FolderProjection queryTree(@InjectParam AuthenticatedUser user,
                                      FolderRequest request) {

        Workspace workspace = workspaceLookup.lookup(request.getRootId());

        try(WorkspaceTransaction tx = beginRead(workspace, user)) {

            ResourceNode rootNode = workspace.getLatestContent(request.getRootId()).getAsNode(tx);
            rootNode.getChildren().addAll(workspace.getFolderIndex().queryFolderItems(tx, rootNode.getId()));

            return new FolderProjection(rootNode);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound(request.getRootId());
        }
    }

    @Override
    public TableData queryTable(@InjectParam AuthenticatedUser user, TableModel tableModel) {
        TableBuilder builder = new TableBuilder(new HrdStoreAccessor(datastore, workspaceLookup, user));
        try {
            return builder.buildTable(tableModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("query/cube")
    @Consumes("application/json")
    @Produces("application/json")
    public List<Bucket> queryCube(@InjectParam AuthenticatedUser user, PivotTableModel tableModel) {
        CubeBuilder builder = new CubeBuilder(new HrdStoreAccessor(datastore, workspaceLookup, user));
        try {
            return builder.buildCube(tableModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<ResourceNode> getOwnedOrSharedWorkspaces(@InjectParam AuthenticatedUser user) {
        return WorkspaceIndex.queryUserWorkspaces(datastore, user, workspaceLookup);
    }


    // TODO Authorization must be added, the requested ResourceGroup must be respected, etc.
    public List<Resource> getUpdates(@InjectParam AuthenticatedUser user, ResourceId workspaceId, long version) {
        ApiProxy.Environment environment = ApiProxy.getCurrentEnvironment();
        Map<ResourceId, Snapshot> snapshots = Maps.newLinkedHashMap();

        Workspace workspace = new Workspace(workspaceId);

        try(WorkspaceTransaction tx = beginRead(workspace, user)) {

            for (Snapshot snapshot : Snapshot.getSnapshotsAfter(tx, version)) {

                // We want the linked list to be sorted based on the most recent insertion of a resource
                snapshots.remove(snapshot.getResourceId());
                snapshots.put(snapshot.getResourceId(), snapshot);

                if (environment.getRemainingMillis() < TIME_LIMIT_MILLISECONDS) {
                    break;
                }
            }

            try {
                List<Resource> resources = Lists.newArrayListWithCapacity(snapshots.size());

                for (Snapshot snapshot : snapshots.values()) {
                    final Authorization authorization;
                    Resource resource = snapshot.get(tx);

                    if (AccessControlRule.CLASS_ID.toString().equals(resource.get("classId"))) {
                        authorization = new Authorization(user, resource);
                    } else {
                        authorization = new Authorization(user, resource.getId(), tx);
                    }

                    if (authorization.canView()) resources.add(resource);
                }

                return resources;
            } catch (EntityNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
