package org.activityinfo.store.hrd.auth;

import org.activityinfo.model.resource.ResourceId;

public class NullAuthorizer implements Authorizer {
    @Override
    public Authorization forResource(ResourceId id) {
        return new IsOwner();
    }
}
