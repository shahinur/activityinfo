package org.activityinfo.service.tables.views;


import org.activityinfo.service.store.ResourceCursor;

/**
 * An object which can receive a stream of {@code Resource}s
 */
public interface InstanceSink {

    void accept(ResourceCursor cursor);

}
