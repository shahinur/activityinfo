package org.activityinfo.service.store;

import org.activityinfo.model.resource.Resource;

public interface StoreLoader {

    /**
     * Creates a new resource
     *
     * @param resource the resource to create
     * @param hasChildren true if the resource has or might have children.
     */
    void create(Resource resource, boolean hasChildren);


    /**
     * Commits this transaction.
     *
     * @return the new version number of this transaction.
     */
    long commit();

}
