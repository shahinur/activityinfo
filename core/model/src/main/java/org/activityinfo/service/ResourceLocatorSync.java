package org.activityinfo.service;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

public interface ResourceLocatorSync {

    /**
     *
     * Retrieves a Resource from the store by id.
     *
     * @param resourceId
     * @return the Resource corresponding to the given {@code resourceId}
     * @throws java.lang.IllegalArgumentException if the resource could not be found, or the
     * user does not have permission to access the resource.
     */
    Resource getResource(ResourceId resourceId);
}
