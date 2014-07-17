package org.activityinfo.core.client;

import org.activityinfo.model.resource.ResourceId;

/**
 * Indicates that the resource was not found on the server
 * or was not visible to the user
 */
public class NotFoundException extends RemoteException {

    private ResourceId resourceId;

    public NotFoundException() {
    }

    public NotFoundException(ResourceId resourceId) {
        super("Resource: " + resourceId);
        this.resourceId = resourceId;
    }


    public ResourceId getResourceId() {
        return resourceId;
    }
}
