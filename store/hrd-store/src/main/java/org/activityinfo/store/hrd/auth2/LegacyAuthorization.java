package org.activityinfo.store.hrd.auth2;

import org.activityinfo.store.hrd.auth.Authorization;

public class LegacyAuthorization implements Authorization {

    private boolean publicRead;

    @Override
    public boolean isOwner() {
        return false;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public boolean canView() {
        return false;
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
