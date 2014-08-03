package org.activityinfo.service.core.tables.views;

import com.google.common.base.Supplier;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.table.ColumnView;

/**
 * Builds a "vertical" view of a single FormField value, or multiple combinations
 * of values.
 */
public interface ColumnViewBuilder extends ResourceSink, Supplier<ColumnView> {

    /**
     * Adds the next resource's value to the column
     *
     * @throws java.lang.IllegalStateException if the get()
     */
    void putResource(Resource resource);

    /**
     * Finalizes this
     */
    void finalizeView();

    /**
     * @returns the
     */
    @Override
    ColumnView get();

}
