package org.activityinfo.store.hrd.auth2;

import org.activityinfo.model.auth.UserPermission;
import org.activityinfo.store.hrd.auth.Authorization;

public class DesignAuthorization implements Authorization {

    private UserPermission userPermission;

    public DesignAuthorization(UserPermission userPermission) {
        this.userPermission = userPermission;
    }

    @Override
    public boolean isOwner() {
        return userPermission.isOwner();
    }

    @Override
    public boolean canUpdate() {
        return userPermission.isDesign();
    }

    @Override
    public boolean canView() {
        return userPermission.isView();
    }

    @Override
    public boolean canCreateChildren() {
        return userPermission.isDesign();
    }

    @Override
    public boolean canModifyAcrs() {
        return false;
    }
}
