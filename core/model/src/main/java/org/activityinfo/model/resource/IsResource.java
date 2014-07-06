package org.activityinfo.model.resource;


/**
 * Marker for objects that wrap an underlying {@code Resource}
 */
public interface IsResource {

    ResourceId getId();

    /**
     * @return a copy of this Object, represented as {@code Resource}
     */
    Resource asResource();

}
