package org.activityinfo.core.client;

import org.activityinfo.core.shared.Cuid;

/**
 * Indicates that the resource was not found on the server
 * or was not visible to the user
 */
public class NotFoundException extends RemoteException {

    private Cuid resourceId;

    public NotFoundException() {
    }

    public NotFoundException(Cuid resourceId) {
        super("Resource: " + resourceId);
        this.resourceId = resourceId;
    }


    public Cuid getResourceId() {
        return resourceId;
    }
}
