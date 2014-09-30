package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.ImplicitTransactionManagementPolicy;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.cubes.CubeBuilder;
import org.activityinfo.service.store.FolderRequest;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tables.TableBuilder;
import org.activityinfo.store.hrd.entity.ReadTransaction;
import org.activityinfo.store.hrd.entity.Snapshot;
import org.activityinfo.store.hrd.entity.UpdateTransaction;
import org.activityinfo.store.hrd.entity.Workspace;
import org.activityinfo.store.hrd.entity.WorkspaceTransaction;
import org.activityinfo.store.hrd.index.WorkspaceIndex;
import org.activityinfo.store.hrd.index.WorkspaceLookup;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.activityinfo.model.resource.Resources.ROOT_ID;

public class HrdResourceStore implements ResourceStore {
    private final static long TIME_LIMIT_MILLISECONDS = 10 * 1000L;


    private final DatastoreService datastore;
    private final WorkspaceLookup workspaceLookup = new WorkspaceLookup();

    public HrdResourceStore() {
        this.datastore = DatastoreServiceFactory.getDatastoreService(DatastoreServiceConfig.Builder
            .withImplicitTransactionManagementPolicy(ImplicitTransactionManagementPolicy.NONE));
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
    public UserResource get(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId) {
        try {
            Workspace workspace = workspaceLookup.lookup(resourceId);

            try(WorkspaceTransaction tx = beginRead(workspace, user)) {
                Authorization authorization = new Authorization(user, resourceId, tx);
                authorization.assertCanView();

                return UserResource.userResource(workspace.getLatestContent(resourceId).get(tx)).
                        setOwner(authorization.isOwner()).
                        setEditAllowed(authorization.canEdit());
            }
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound(resourceId);
        }
    }

    @Override
    public List<Resource> getAccessControlRules(@InjectParam AuthenticatedUser user,
                                                @PathParam("id") ResourceId resourceId) {
        final Workspace workspace = workspaceLookup.lookup(resourceId);

        try (WorkspaceTransaction tx = beginRead(workspace, user)) {
            return Lists.newArrayList(Iterables.transform(workspace.getAcrIndex().queryRules(tx, resourceId),
                new Function<ResourceId, Resource>() {
                @Override
                public Resource apply(ResourceId resourceId) {
                    try {
                        return workspace.getLatestContent(resourceId).get(tx);
                    } catch (EntityNotFoundException e) {
                        throw new ResourceNotFound(resourceId);
                    }
                }
            }));
        }
    }

    @PUT
    @Path("resource/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public UpdateResult put(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId, Resource resource) {
       if (resourceId.equals(resource.getId())) {
           return put(user, resource);
       } else {
           throw new WebApplicationException(BAD_REQUEST);
       }
    }

    /**
     * Deletes {@code Resource}s from the store
     *
     * @param user      authenticated user
     * @param resources resources
     * @return result whether resource was deleted or not
     */
    @DELETE
    @Path("resources")
    @Consumes("application/json")
    @Produces("application/json")
    @Override
    public List<UpdateResult> delete(@InjectParam AuthenticatedUser user, List<ResourceId> resources) {

        // first assert whether "whole" request can be handled, to not be trapped in the UNAUTHORIZED
        // response in the middle of processing (other solution may be always return UpdateResult, but we leave this strategy for now)
        assertCanDelete(user, resources);

        List<UpdateResult> result = Lists.newArrayList();
        for (ResourceId resourceId : resources) {
            result.add(deleteResource(user, resourceId));
        }
        return result;
    }

    private UpdateResult deleteResource(AuthenticatedUser user, ResourceId resourceId) {
        Workspace workspace = workspaceLookup.lookup(resourceId);

        try (WorkspaceTransaction tx = begin(workspace, user)) {

            long newVersion = workspace.deleteResourceTree(tx, resourceId);

            return UpdateResult.committed(resourceId, newVersion);
        } catch (EntityNotFoundException e) {
            return UpdateResult.rejected(resourceId);
        }
    }

    private void assertCanDelete(AuthenticatedUser user, List<ResourceId> resources) {
        for (ResourceId resourceId : resources) {
            Workspace workspace = workspaceLookup.lookup(resourceId);

            try (WorkspaceTransaction tx = begin(workspace, user)) {

                Authorization authorization = new Authorization(user, resourceId, tx);
                authorization.assertCanEdit();

            }
        }
    }

    @Override
    public UpdateResult put(AuthenticatedUser user, Resource resource) {
        Workspace workspace = workspaceLookup.lookup(resource.getId());

        try (WorkspaceTransaction tx = begin(workspace, user)) {
            try {
                workspace.getLatestContent(resource.getId()).get(tx);
            } catch (EntityNotFoundException e) {
                return create(tx, user, resource);
            }

            Authorization authorization = new Authorization(user, resource.getId(), tx);

            authorization.assertCanEdit();

            long newVersion = workspace.updateResource(tx, resource);
            tx.commit();

            return UpdateResult.committed(resource.getId(), newVersion);
        }
    }

    @Override
    public UpdateResult create(AuthenticatedUser user, Resource resource) {
        if(ROOT_ID.equals(resource.getOwnerId())) {
            if(user.isAnonymous()) {
                throw new WebApplicationException(UNAUTHORIZED);
            }

            Workspace workspace = new Workspace(resource.getId());
            try(WorkspaceTransaction tx = begin(workspace, user)) {
                long newVersion = workspace.createWorkspace(tx, resource);
                tx.commit();

                workspaceLookup.cache(resource.getId(), workspace);

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

        authorization.assertCanEdit();

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

    }


    @Override
    public FolderProjection queryTree(@InjectParam AuthenticatedUser user,
                                      FolderRequest request) {

        Workspace workspace = workspaceLookup.lookup(request.getRootId());

        try(WorkspaceTransaction tx = beginRead(workspace, user)) {
            Authorization rootNodeAuthorization = new Authorization(user, request.getRootId(), tx);
            rootNodeAuthorization.assertCanView();

            ResourceNode rootNode = workspace.getLatestContent(request.getRootId()).getAsNode(tx).
                    setEditAllowed(rootNodeAuthorization.canEdit()).
                    setOwner(rootNodeAuthorization.isOwner());

            for (ResourceNode child : workspace.getFolderIndex().queryFolderItems(tx, rootNode.getId())) {
                Authorization childAuthorization = rootNodeAuthorization.ofChild(child.getId());
                child.setEditAllowed(childAuthorization.canEdit()).
                        setOwner(childAuthorization.isOwner());
                rootNode.getChildren().add(child);
            }

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
        return WorkspaceIndex.queryUserWorkspaces(user);
    }

    @Override
    public List<Resource> getUpdates(@InjectParam AuthenticatedUser user, ResourceId workspaceId, long version) {
        Environment environment = ApiProxy.getCurrentEnvironment();
        Map<ResourceId, Snapshot> snapshots = Maps.newLinkedHashMap();
        Map<ResourceId, Authorization> authorizations = Maps.newHashMap();
        Workspace workspace = new Workspace(workspaceId);

        if (version < 0) version = 0;

        try(WorkspaceTransaction tx = beginRead(workspace, user)) {

            for (Snapshot snapshot : Snapshot.getSnapshotsAfter(tx, version)) {
                ResourceId resourceId = snapshot.getResourceId();

                // We want the linked list to be sorted based on the most recent insertion of a resource
                snapshots.remove(resourceId);
                snapshots.put(resourceId, snapshot);

                if (authorizations.get(resourceId) == null) {
                    authorizations.put(resourceId, new Authorization(user, resourceId, tx));
                }

                if (environment.getRemainingMillis() < TIME_LIMIT_MILLISECONDS) {
                    break;
                }
            }

            List<Resource> resources = Lists.newArrayListWithCapacity(snapshots.size());

            for (Snapshot snapshot : snapshots.values()) {
                final Authorization authorization;
                Resource resource = snapshot.get(tx);

                if (AccessControlRule.CLASS_ID.equals(resource.getValue().getClassId())) {
                    final Optional<Authorization> oldAuthorization;
                    final Optional<Snapshot> optionalSnapshot = Snapshot.getSnapshotAsOf(tx, resource.getId(), version);

                    authorization = new Authorization(user, resource);

                    if (optionalSnapshot.isPresent()) {
                        oldAuthorization = Optional.of(new Authorization(user, optionalSnapshot.get().get(tx)));
                    } else {
                        oldAuthorization = Optional.absent();
                    }

                    // TODO Deal with the effects of changed authorizations correctly
                    for (Resource newlyAuthorizedResource : applyAuthorization(oldAuthorization, authorization, tx)) {
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
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Collection<Resource> applyAuthorization(Optional<Authorization> oldAuthorization,
                                                           Authorization newAuthorization, WorkspaceTransaction tx) {
        if (newAuthorization.canViewNowButNotAsOf(oldAuthorization)) {
            ResourceId resourceId = newAuthorization.getResourceId();

            if (resourceId != null) {
                final Collection<Resource> result = Lists.newArrayList();

                for (ResourceId element : descendIdTree(newAuthorization, resourceId, tx)) {
                    try {
                        result.add(tx.getWorkspace().getLatestContent(element).get(tx));
                    } catch (EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }

                return result;
            }
        }

        return Collections.emptySet();
    }

    // This method recursively descends the ID tree of a resource node, including only nodes with the same authorization
    private static List<ResourceId> descendIdTree(Authorization authorization, ResourceId id, WorkspaceTransaction tx) {
        final List<ResourceId> result = Lists.newArrayList(id);

        if (authorization != null && id != null && tx != null) {
            ResourceId authorizationId = authorization.getId();
            AuthenticatedUser authenticatedUser = tx.getUser();
            Workspace workspace = tx.getWorkspace();

            if (authorizationId != null && authenticatedUser != null && workspace != null) {
                try {
                    for (ResourceId child : workspace.getLatestContent(id).getChildIds(tx)) {
                        if (authorizationId.equals(new Authorization(authenticatedUser, child, tx).getId())) {
                            result.addAll(descendIdTree(authorization, child, tx));
                        }
                    }
                } catch (EntityNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return result;
    }
}
