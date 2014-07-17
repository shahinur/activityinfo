package org.activityinfo.server.util.config;

import com.google.common.collect.Maps;
import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Map;

/**
 * Simple servlet to allow AppEngine administrators to define the configuration
 * properties for this instance. This makes it possible to set config params,
 * like legacy keys, etc, seperately from the (public) source code.
 * <p/>
 * <p/>
 * This servlet stores the text of a properties file to the Datastore
 */
@Path("/admin/config")
public class AppengineConfigResource {

    public static final String END_POINT = "/admin/config";

    @GET @Produces(MediaType.TEXT_HTML)
    public Viewable getPage() {
        Map<String, String> model = Maps.newHashMap();
        model.put("currentConfig", AppEngineConfig.getPropertyFile());

        return new Viewable("/page/Config.ftl", model);
    }

    @POST
    public Response update(@Context UriInfo uri, @FormParam("config") String config) {
        AppEngineConfig.setPropertyFile(config);

        return Response.seeOther(uri.getRequestUri()).build();
    }

}
