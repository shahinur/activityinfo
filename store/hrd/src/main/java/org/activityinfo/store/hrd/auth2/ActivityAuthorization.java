package org.activityinfo.store.hrd.auth2;

import org.activityinfo.model.auth.UserPermission;
import org.activityinfo.store.hrd.auth.Authorization;

public class ActivityAuthorization implements Authorization {

    private final UserPermission userPermission;

    public ActivityAuthorization(UserPermission userPermission) {
        this.userPermission = userPermission;
    }

    @Override
    public boolean isOwner() {
        return false;
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
        return userPermission.isEdit();
    }

    @Override
    public boolean canModifyAcrs() {
        return false;
    }
}
