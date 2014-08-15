package org.activityinfo.service.store;

import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.ResourceTree;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;

import javax.ws.rs.*;
import java.util.List;
import java.util.Set;

@Path("/service/store")
public interface ResourceStore {


    /**
     * Fetches the latest version of the resource from the store.
     */
    @GET
    @Path("resource/{id}")
    @Produces("application/json")
    Resource get(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId);

    /**
     * Fetches the latest version of the resources from the store
     */
    Set<Resource> get(@InjectParam AuthenticatedUser user, Set<ResourceId> resourceIds);


    /**
     * Creates or updates a {@code Resource} within the store.
     */
    @PUT
    @Path("resource/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    UpdateResult put(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId, Resource resource);


    /**
     * Creates or updates a {@code Resource} within the store.
     *
     * <p>Non Jax-rs convenience method
     *
     * @param user
     * @param resource
     * @return
     */
    UpdateResult put(AuthenticatedUser user, Resource resource);


    /**
     * Fetches an outline of Resources, returning only their id and label.
     */
    @POST
    @Path("query/tree")
    @Produces("application/json")
    ResourceTree queryTree(@InjectParam AuthenticatedUser user, ResourceTreeRequest request);

    /**
     * Fetches an outline of Resources, returning only their id and label.
     */
    @POST
    @Path("query/table")
    @Consumes("application/json")
    @Produces("application/json")
    TableData queryTable(@InjectParam AuthenticatedUser user, TableModel tableModel);


    /**
     *
     * @return a list of resources owned or explicitly shared with a
     * given user
     */
    @GET
    @Path("query/roots")
    @Produces("application/json")
    List<ResourceNode> getUserRootResources(@InjectParam AuthenticatedUser user);
}
