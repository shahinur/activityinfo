package org.activityinfo.server.entity.auth;

import com.google.common.base.Preconditions;
import org.activityinfo.api.shared.auth.AuthenticatedUser;
import org.activityinfo.server.database.hibernate.entity.SchemaElement;
import org.activityinfo.server.database.hibernate.entity.UserDatabase;
import org.activityinfo.server.database.hibernate.entity.UserPermission;

/**
 * Checks whether the requesting user is authorized to change the given entity.
 */
public class DesignAuthorizationHandler implements AuthorizationHandler<SchemaElement> {

    @Override
    public boolean isAuthorized(AuthenticatedUser requestingUser, SchemaElement entity) {
        Preconditions.checkNotNull(requestingUser, "requestingUser");

        UserDatabase database = entity.findOwningDatabase();
        if (database.getOwner().getId() == requestingUser.getId()) {
            return true;
        }
        for (UserPermission permission : database.getUserPermissions()) {
            if (permission.getUser().getId() == requestingUser.getId() && permission.isAllowDesign()) {
                return true;
            }
        }
        return false;
    }
}