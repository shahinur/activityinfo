package org.activityinfo.service.store;

import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/service/store")
public final class ResourceStoreRestfulService {

    private final ResourceStore service;

    @Inject
    public ResourceStoreRestfulService(ResourceStore service) {
        this.service = service;
    }

    /**
     * Fetches the latest version of the resource from the store.
     */
    @GET
    @Path("resource/{id}")
    @Produces("application/json")
    public UserResource get(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId) {
        assertNotNull(resourceId);
        return service.get(user, resourceId);
    }

    @GET
    @Path("resource/{id}/acr")
    @Produces("application/json")
    public List<Resource> getAccessControlRules(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId) {
        assertNotNull(resourceId);
        return service.getAccessControlRules(user, resourceId);
    }


    /**
     * Updates a {@code Resource} within the store.
     */
    @PUT
    @Path("resource/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public UpdateResult put(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId, Resource resource) {
        if (!resourceId.equals(resource.getId())) {
            throw new WebApplicationException(Response.status(BAD_REQUEST)
                .entity("Resource Id of entity does not match path").build());
        }
        return service.put(user, resource);
    }

    /**
     * Deletes {@code Resource}s from the store
     *
     * @param user      authenticated user
     * @param resourceId resource id
     * @return result whether resource was deleted or not
     */
    @DELETE
    @Path("resource/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public UpdateResult delete(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId) {
        assertNotNull(resourceId);
        return service.delete(user, resourceId);
    }


    /**
     * Creates a {@code Resource} within the store.
     *
     * @param user
     * @param resource
     * @return
     */
    @POST
    @Path("resources")
    @Consumes("application/json")
    @Produces("application/json")
    public UpdateResult create(@InjectParam AuthenticatedUser user, Resource resource) {
        return service.create(user, resource);
    }

    /**
     * Fetches an outline of Resources, returning only their id and label.
     */
    @POST
    @Path("query/tree")
    @Produces("application/json")
    public FolderProjection queryTree(@InjectParam AuthenticatedUser user, FolderRequest request) {
        return service.queryTree(user, request);
    }


    /**
     * Fetches an outline of Resources, returning only their id and label.
     */
    @POST
    @Path("query/table")
    @Consumes("application/json")
    @Produces("application/json")
    public TableData queryTable(@InjectParam AuthenticatedUser user, TableModel tableModel) {
        return service.queryTable(user, tableModel);
    }

    @POST
    @Path("query/cube")
    @Consumes("application/json")
    @Produces("application/json")
    public List<Bucket> queryCube(@InjectParam AuthenticatedUser user, PivotTableModel tableModel) {
        return service.queryCube(user, tableModel);
    }


    /**
     *
     * @return a list of workspaces owned or explicitly shared with a
     * given user
     */
    @GET
    @Path("query/roots")
    @Produces("application/json")
    public List<ResourceNode> getOwnedOrSharedWorkspaces(@InjectParam AuthenticatedUser user) {
        return service.getOwnedOrSharedWorkspaces(user);
    }

    @GET
    @Path("query/updates/{workspaceId}")
    @Produces("application/json")
    public List<Resource> getUpdates(@InjectParam AuthenticatedUser user, @PathParam("workspaceId") ResourceId workspaceId,
                              @QueryParam("version") long version) {
        return service.getUpdates(user, workspaceId, version);
    }

    private static void assertNotNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) throw new WebApplicationException(BAD_REQUEST);
        }
    }

}
