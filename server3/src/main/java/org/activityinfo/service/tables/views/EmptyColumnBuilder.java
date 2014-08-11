package org.activityinfo.service.tables.views;

import com.google.common.base.Supplier;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;

public class EmptyColumnBuilder implements ColumnViewBuilder {

    private final ColumnType columnType;
    private final Supplier<Integer> rowCount;

    private EmptyColumnView view = null;

    public EmptyColumnBuilder(ColumnType columnType, Supplier<Integer> rowCount) {
        this.columnType = columnType;
        this.rowCount = rowCount;
    }

    @Override
    public void putResource(Resource resource) {
    }

    @Override
    public void finalizeView() {

    }

    @Override
    public ColumnView get() {
        if(view == null) {
            view = new EmptyColumnView(columnType, rowCount.get());
        }
        return view;
    }
}
