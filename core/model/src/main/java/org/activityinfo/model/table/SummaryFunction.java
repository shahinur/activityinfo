package org.activityinfo.model.table;

import java.util.List;
import java.util.Set;

public interface SummaryFunction {

    /**
     * Creates a new ColumnView that combines one or more
     * columnViews
     * @param columnViews at least one ColumnView
     */
    ColumnView combine(Set<ColumnView> columnViews);

}
