package org.activityinfo.service.store;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import java.util.Iterator;

public interface ResourceStore {

    /**
     * Opens a cursor over a list of instances belong to a FormClass
     */
    Iterator<Resource> openCursor(ResourceId formClassId);

    /**
     * Fetches the latest version of the resource from the store.
     */
    Resource get(ResourceId resourceId);

    void createResource(ResourceId resourceId, Resource resource);

    Resource get(AuthenticatedUser user, ResourceId resourceId);

    void put(AuthenticatedUser user, Resource resource);


}
