package org.activityinfo.server.endpoint.odk;

import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;

import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Logger;

@Path("/formList")
public class FormListResource {
    private static final Logger LOGGER = Logger.getLogger(FormListResource.class.getName());

    private Provider<AuthenticatedUser> authProvider;

    @Inject
    public FormListResource(OdkAuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    @GET @Produces(MediaType.TEXT_XML)
    public Response formList(@Context UriInfo info) throws Exception {
        AuthenticatedUser user = authProvider.get();

        LOGGER.finer("ODK formlist requested by " + user.getEmail() + " (" + user.getId() + ")");

        // TODO: provide form list from ResourceStore
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }
}
