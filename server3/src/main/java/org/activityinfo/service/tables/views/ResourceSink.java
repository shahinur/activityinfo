package org.activityinfo.service.tables.views;


import org.activityinfo.model.resource.Resource;

/**
 * An object which can receive a stream of {@code Resource}s
 */
public interface ResourceSink {

    void putResource(Resource resource);

}
