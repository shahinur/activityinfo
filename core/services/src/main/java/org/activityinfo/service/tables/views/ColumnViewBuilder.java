package org.activityinfo.service.tables.views;

import com.google.common.base.Supplier;
import org.activityinfo.model.form.FormEvalContext;
import org.activityinfo.model.table.ColumnView;

/**
 * Builds a "vertical" view of a single FormField value, or multiple combinations
 * of values.
 */
public interface ColumnViewBuilder extends FormSink, Supplier<ColumnView> {

    /**
     * Adds the next form instance's value to the column
     *
     * @throws java.lang.IllegalStateException if the get()
     */
    void accept(FormEvalContext instance);

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
