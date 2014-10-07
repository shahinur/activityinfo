package org.activityinfo.service.tables.views;


import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;

/**
 * An object which can receive a stream of {@code Resource}s
 */
public interface InstanceSink {

    void accept(ResourceId resourceId, Record value);

}
