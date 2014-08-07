package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sun.jersey.api.view.Viewable;
import org.activityinfo.legacy.shared.auth.AuthenticatedUser;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.server.command.DispatcherSync;

import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Map;
import java.util.logging.Logger;

@Path("/formList")
public class FormListResource {
    private static final Logger LOGGER = Logger.getLogger(FormListResource.class.getName());

    private Provider<AuthenticatedUser> authProvider;
    private DispatcherSync dispatcher;

    @Inject
    public FormListResource(OdkAuthProvider authProvider, DispatcherSync dispatcher) {
        this.authProvider = authProvider;
        this.dispatcher = dispatcher;
    }

    @GET @Produces(MediaType.TEXT_XML)
    public Response formList(@Context UriInfo info) throws Exception {
        AuthenticatedUser user = authProvider.get();

        LOGGER.finer("ODK formlist requested by " + user.getEmail() + " (" + user.getId() + ")");

        Map<String, Object> map = Maps.newHashMap();
        map.put("schema", dispatcher.execute(new GetSchema()));
        map.put("host", info.getBaseUri().toString());

        return Response.ok(new Viewable("/odk/formList.ftl", map)).build();
    }
}
