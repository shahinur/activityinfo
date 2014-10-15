package org.activityinfo.store.hrd.auth;

import org.activityinfo.model.resource.ResourceId;

public interface Authorizer {

    Authorization forResource(ResourceId id);
}
