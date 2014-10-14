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
        if(!authorizer.forResource(resourceId).canUpdate()) {
            throwUnauthorized(resourceId, "update");
        }
    }

    public void assertCanView(ResourceId resourceId) {
        if (!authorizer.forResource(resourceId).canView()) {
            throwUnauthorized(resourceId, "view");
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
