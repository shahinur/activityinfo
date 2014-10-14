package org.activityinfo.store.hrd.dao;

import org.activityinfo.model.resource.ResourceId;

/**
 * Exception thrown when the user tries to create the same resource multiple times.
 */
public class ResourceExistsException extends RuntimeException {

    private ResourceId resourceId;
    private long version;

    public ResourceExistsException(ResourceId resourceId, long version) {
        super("Resource " + resourceId + " was already committed on version " + version);
        this.resourceId = resourceId;
        this.version = version;
    }

    public ResourceId getResourceId() {
        return resourceId;
    }

    /**
     *
     * @return the version on which the resource was committed
     */
    public long getVersion() {
        return version;
    }
}
