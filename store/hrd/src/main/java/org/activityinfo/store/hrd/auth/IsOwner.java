package org.activityinfo.store.hrd.auth;

public class IsOwner implements Authorization {

    @Override
    public boolean isOwner() {
        return true;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public boolean canView() {
        return true;
    }

    @Override
    public boolean canCreateChildren() {
        return true;
    }

    @Override
    public boolean canModifyAcrs() {
        return true;
    }
}
