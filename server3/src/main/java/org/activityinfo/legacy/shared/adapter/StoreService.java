package org.activityinfo.legacy.shared.adapter;

import org.activityinfo.service.store.ResourceNode;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/service/store")
public interface StoreService extends RestService {

    /**
     * Retrieves the current user's root resources: those resources which
     * they own or have been explicitly shared with them.
     */
    @GET
    @Path("roots")
    List<ResourceNode> getRootResources(MethodCallback<List<ResourceNode>> roots);

}
