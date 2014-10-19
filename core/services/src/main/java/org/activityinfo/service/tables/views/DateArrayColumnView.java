package org.activityinfo.service.tables.views;

import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DateArrayColumnView implements ColumnView, Serializable {

    private List<Date> values;

    public DateArrayColumnView(List<Date> values) {
        this.values = values;
    }

    @Override
    public ColumnType getType() {
        return ColumnType.DATE;
    }

    @Override
    public int numRows() {
        return values.size();
    }

    @Override
    public Object get(int row) {
        return values.get(row);
    }

    @Override
    public double getDouble(int row) {
        return Double.NaN;
    }

    @Override
    public String getString(int row) {
        return values.get(row).toString();
    }

    @Override
    public Date getDate(int row) {
        return values.get(row);
    }

    @Override
    public int getBoolean(int row) {
        return NA;
    }
}
