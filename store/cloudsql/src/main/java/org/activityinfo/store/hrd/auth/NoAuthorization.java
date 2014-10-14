package org.activityinfo.store.hrd.auth;

class NoAuthorization implements Authorization {
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
}
