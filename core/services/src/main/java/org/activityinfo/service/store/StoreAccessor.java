package org.activityinfo.service.store;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import java.sql.SQLException;

/**
 * Interface to accessing the store on behalf of an authenticated user
 */
public interface StoreAccessor extends AutoCloseable {

    /**
     * Opens a cursor over a list of instances belong to a FormClass
     */
    ResourceCursor openCursor(ResourceId formClassId) throws Exception;

    Resource get(ResourceId formClassId) throws Exception;

    void close();


}
