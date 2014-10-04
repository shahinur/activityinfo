package org.activityinfo.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

/**
 * Simple endpoint to allow the client to test connectivity with the server.
 */
@Path("/service/ping")
public class PingService {

    @GET
    public Response ping() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        return Response.ok().entity("OK").cacheControl(cacheControl).build();
    }

}
