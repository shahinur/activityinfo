package org.activityinfo.service.store;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import java.util.Iterator;

public interface ResourceStore {

    /**
     * Opens a cursor over a list of instances belong to a FormClass
     */
    ResourceCursor openCursor(ResourceId formClassId);

    /**
     * Fetches the latest version of the resource from the store.
     */
    Resource get(ResourceId resourceId);


    /**
     * Fetches an outline of Resources, returning only their id and label.
     */
    ResourceTree queryTree(ResourceTreeRequest request);

    /**
     * Creates a new resource inside the store.
     */
    UpdateResult createResource(ResourceId userId, Resource resource);


    /**
     * Updates an existing resource
     */
    UpdateResult updateResource(ResourceId userId, Resource resource);
}
