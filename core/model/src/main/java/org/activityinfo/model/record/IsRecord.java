package org.activityinfo.model.record;

/**
 * Marker for objects that serialize to an underlying {@code Resource}
 */
public interface IsRecord {

    /**
     * @return this object serialized as a {@code Record}
     */
    Record asRecord();

}
