package org.activityinfo.io.load;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;

public class FormImportOptions {
    private AuthenticatedUser user;
    private ResourceId ownerId;

    public AuthenticatedUser getUser() {
        return user;
    }

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }

    public ResourceId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ResourceId ownerId) {
        this.ownerId = ownerId;
    }
}
