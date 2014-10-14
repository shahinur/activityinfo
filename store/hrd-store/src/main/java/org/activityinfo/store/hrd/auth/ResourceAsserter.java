package org.activityinfo.store.hrd.auth;

import org.activityinfo.model.resource.ResourceId;

public class ResourceAsserter {
    private Authorization authorization;
    private ResourceId resourceId;
    private AuthorizationAsserter asserter;

    ResourceAsserter(AuthorizationAsserter asserter, ResourceId resourceId, Authorization authorization) {
        this.asserter = asserter;
        this.authorization = authorization;
        this.resourceId = resourceId;
    }

    public void assertCanUpdate() {
        if(!authorization.canUpdate()) {
            asserter.throwUnauthorized(resourceId, "edit");
        }
    }

    public void assertCanCreateChildren() {
        if(!authorization.canCreateChildren()) {
            asserter.throwUnauthorized(resourceId, "edit");
        }
    }

    public void assertCanView() {
        if(!authorization.canView()) {
            asserter.throwUnauthorized(resourceId, "view");
        }
    }

    public Authorization get() {
        return authorization;
    }

    public void assertCanViewAcrs() {
        if(!authorization.isOwner()) {
            asserter.throwUnauthorized(resourceId, "view acrs");
        }
    }
}
