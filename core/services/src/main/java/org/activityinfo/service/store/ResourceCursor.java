package org.activityinfo.service.store;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;

public interface ResourceCursor extends AutoCloseable {

    boolean next();

    ResourceId getResourceId();

    Record getRecord();

    long getVersion();

    long getInitialVersion();

}
