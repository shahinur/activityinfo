package org.activityinfo.model.table.columns;

import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.util.Date;

public class DoubleArrayColumnView implements ColumnView {
    private double[] values;

    public DoubleArrayColumnView(double[] values) {
        this.values = values;
    }

    @Override
    public ColumnType getType() {
        return ColumnType.NUMBER;
    }

    @Override
    public int numRows() {
        return values.length;
    }

    @Override
    public Object get(int row) {
        double value = values[row];
        if(Double.isNaN(value)) {
            return null;
        } else {
            return value;
        }
    }

    @Override
    public double getDouble(int row) {
        return values[row];
    }

    @Override
    public String getString(int row) {
        return null;
    }

    @Override
    public Date getDate(int row) {
        return null;
    }
}
