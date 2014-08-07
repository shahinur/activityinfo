package org.activityinfo.model.resource;

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


    /**
     * Fetches an outline of Resources, returning only their id and label.
     */
    ResourceTree queryTree(ResourceTreeRequest request);
}
