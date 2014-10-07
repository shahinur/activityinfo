package org.activityinfo.store.hrd.auth;

public class IsOwner extends Authorization {

    @Override
    public boolean isOwner() {
        return true;
    }

    @Override
    public boolean canEdit() {
        return true;
    }

    @Override
    public boolean canView() {
        return true;
    }
}
