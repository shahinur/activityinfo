package org.activityinfo.server.entity.auth;

import com.google.inject.Inject;
import org.activityinfo.service.auth.AuthenticatedUser;
import org.activityinfo.server.command.handler.PermissionOracle;
import org.activityinfo.server.database.hibernate.entity.Site;

/**
 * Checks the requesting user's authorization to modify / create a Site
 */
public class SiteAuthorizationHandler implements AuthorizationHandler<Site> {

    private final PermissionOracle permissionOracle;

    @Inject
    public SiteAuthorizationHandler(PermissionOracle permissionOracle) {
        this.permissionOracle = permissionOracle;
    }

    @Override
    public boolean isAuthorized(AuthenticatedUser requestingUser, Site site) {
        return permissionOracle.isEditAllowed(site, requestingUser);
    }
}
