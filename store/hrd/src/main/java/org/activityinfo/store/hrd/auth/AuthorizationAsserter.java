package org.activityinfo.store.hrd.auth;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.UnauthorizedException;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class AuthorizationAsserter {

    private static final Logger LOGGER = Logger.getLogger(AuthorizationAsserter.class.getName());

    private AuthenticatedUser user;
    private Authorizer authorizer;

    public AuthorizationAsserter(AuthenticatedUser user, Authorizer authorizer) {
        this.user = user;
        this.authorizer = authorizer;
    }

    public void assertCanUpdate(ResourceId resourceId) {
        if(resourceId.asString().startsWith("_acr")) {
            assertCanModifyAcrs(resourceId);

        } else {

            // Users can't modify application resources!
            // They're not store in the database at all...
            assertNotApplicationDefined(resourceId);

            if (!authorizer.forResource(resourceId).canUpdate()) {
                throwUnauthorized(resourceId, "update");
            }
        }
    }

    private void assertNotApplicationDefined(ResourceId resourceId) {
        if(resourceId.isApplicationDefined()) {
            LOGGER.log(Level.SEVERE, String.format("User id=%d attempted to modify/store an application-defined resource %s",
                    user.getId(), resourceId));

            throw new UnauthorizedException();
        }
    }

    public void assertCanView(ResourceId resourceId) {
        if (!authorizer.forResource(resourceId).canView()) {
            throwUnauthorized(resourceId, "view");
        }
    }

    public void assertCanCreateChildren(ResourceId resourceId) {
        if (!authorizer.forResource(resourceId).canCreateChildren()) {
            throwUnauthorized(resourceId, "createChildren");
        }
    }

    public void assertCanModifyAcrs(ResourceId resourceId) {
        if (!authorizer.forResource(resourceId).canModifyAcrs()) {
            throwUnauthorized(resourceId, "createChildren");
        }
    }

    public ResourceAsserter forResource(ResourceId resourceId) {
        return new ResourceAsserter(this, resourceId, authorizer.forResource(resourceId));
    }

    void throwUnauthorized(ResourceId resourceId, String operation) {
        LOGGER.log(Level.SEVERE, String.format("User id=%d attempted operation '%s' on resource %s",
                user.getId(), operation, resourceId));
        throw new UnauthorizedException();
    }

}
