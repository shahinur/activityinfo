package org.activityinfo.service.tables;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.tree.FormClassProvider;

/**
 * Internal interface used to construct table queries.
 */
public interface StoreAccessor extends FormClassProvider {


    /**
     * Retrieves a single resource from the ResourceStore.
     * @param resourceId the id of the resource to retrieve
     * @return the Resource
     * @throws
     */
    Resource get(ResourceId resourceId);


    /**
     * Opens a cursor over a list of ALL instances belong to a FormClass.
     * Authorization is NOT applied to this cursor, the table builder should
     * add the authorization expression as part of the filter.
     */
    ResourceCursor openCursor(ResourceId formClassId) throws Exception;


}
