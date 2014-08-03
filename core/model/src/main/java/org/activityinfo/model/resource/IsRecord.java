package org.activityinfo.model.resource;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Marker for objects that serialize to an underlying {@code Resource}
 */
public interface IsRecord {

    /**
     * @return this object serialized as a {@code Record}
     */
    Record asRecord();


}
