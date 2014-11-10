package org.activityinfo.server.endpoint.odk;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.server.command.ResourceLocatorSyncImpl;
import org.activityinfo.server.endpoint.odk.xform.XForm;
import org.activityinfo.server.endpoint.odk.xform.XFormBuilder;
import org.activityinfo.service.store.ResourceNotFound;

import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;


@Path("/activityForm")
public class FormResource {

    private static final Logger LOGGER = Logger.getLogger(FormResource.class.getName());

    private Provider<AuthenticatedUser> authProvider;
    private ResourceLocatorSyncImpl locator;
    private OdkFormFieldBuilderFactory factory;
    private AuthenticationTokenService authenticationTokenService;

    @Inject
    public FormResource(ResourceLocatorSyncImpl locator, OdkAuthProvider authProvider, OdkFormFieldBuilderFactory factory,
                        AuthenticationTokenService authenticationTokenService) {
        this.locator = locator;
        this.authProvider = authProvider;
        this.factory = factory;
        this.authenticationTokenService = authenticationTokenService;
    }

    @VisibleForTesting
    FormResource(ResourceLocatorSyncImpl locator, Provider<AuthenticatedUser> authProvider, OdkFormFieldBuilderFactory factory,
                 AuthenticationTokenService authenticationTokenService) {
        this.authProvider = authProvider;
        this.locator = locator;
        this.factory = factory;
        this.authenticationTokenService = authenticationTokenService;
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    public Response form(@QueryParam("id") int id) {

        AuthenticatedUser user = authProvider.get();

        LOGGER.finer("ODK activity form " + id + " requested by " +
                     user.getEmail() + " (" + user.getId() + ")");

        FormClass formClass;
        try {
            formClass = locator.getFormClass(CuidAdapter.activityFormClass(id));
        } catch (ResourceNotFound resourceNotFound) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        String authenticationToken = authenticationTokenService
                .createAuthenticationToken(user.getId(), formClass.getId());

        XForm xForm = new XFormBuilder(factory)
                .setUserId(authenticationToken)
                .build(formClass);

        return Response.ok(xForm).build();
    }


}
