package org.activityinfo.store.hrd.auth2;

import org.activityinfo.model.auth.UserPermission;
import org.activityinfo.store.hrd.auth.Authorization;

public class SiteAuthorization implements Authorization {

    private UserPermission userPermission;

    public SiteAuthorization(UserPermission userPermission) {
        this.userPermission = userPermission;
    }

    @Override
    public boolean isOwner() {
        return userPermission.isOwner();
    }

    @Override
    public boolean canUpdate() {
        return userPermission.isEdit();
    }

    @Override
    public boolean canView() {
        return userPermission.isView();
    }

    @Override
    public boolean canCreateChildren() {
        return false;
    }

    @Override
    public boolean canModifyAcrs() {
        return false;
    }
}
