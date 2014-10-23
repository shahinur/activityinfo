package org.activityinfo.service.store;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;

/**
 * Iterates over the instances of a FormClass.
 *
 * <p><strong>IMPORTANT:</strong> This is an internal interface that does <strong>not</strong>
 * apply permissions; the caller must perform their own checks.</p>
 */
public interface ResourceCursor extends AutoCloseable {

    /**
     * Advances the cursor to the next instance.
     *
     * @return true if there are more instances
     */
    boolean next();

    /**
     *
     * @return the {@code ResourceId} of the current instance
     */
    ResourceId getResourceId();

    /**
     *
     * @return the record containing the properties of the current instance.
     */
    Record getRecord();

    /**
     *
     * @return the version number of the current instance
     */
    long getVersion();

    /**
     *
     * @return the version number during which the current resource was first created.
     */
    long getInitialVersion();

    /**
     *
     * @return true if the current resource has been deleted. This <strong>does</strong>
     * not check if the current resource's ancestor has been deleted: the caller must
     * perform this check themselves.
     */
    boolean isDeleted();

}
