package org.activityinfo.store.hrd.auth;

public class NoAuthorization implements Authorization {
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
