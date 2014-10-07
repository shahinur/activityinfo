package org.activityinfo.store.hrd.auth;

class NoAuthorization extends Authorization {
    @Override
    public boolean isOwner() {
        return false;
    }

    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public boolean canView() {
        return false;
    }
}
