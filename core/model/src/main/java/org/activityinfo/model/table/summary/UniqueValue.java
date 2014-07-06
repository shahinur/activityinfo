package org.activityinfo.model.table.summary;

import com.google.common.base.Preconditions;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.SummaryFunction;
import org.activityinfo.model.table.columns.EmptyColumnView;

import java.util.List;
import java.util.Set;

/**
 * A summarizing function which returns the unique value
 * from a set of ColumnViews, or null if there are no or multiple
 * values
 */
public enum UniqueValue implements SummaryFunction {

    INSTANCE {
        @Override
        public ColumnView combine(Set<ColumnView> columnViews) {
            Preconditions.checkArgument(!columnViews.isEmpty());

            if(columnViews.size() == 1) {
                return columnViews.iterator().next();
            } else {
                return new UniqueValueColumnView(columnViews);
            }
        }
    }
}
