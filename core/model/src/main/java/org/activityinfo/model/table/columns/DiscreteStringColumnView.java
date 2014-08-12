package org.activityinfo.model.table.columns;

import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.util.Date;

public class DiscreteStringColumnView implements ColumnView {

    private String[] labels;
    private int[] values;

    public DiscreteStringColumnView(String[] labels, int[] values) {
        this.labels = labels;
        this.values = values;
    }

    @Override
    public ColumnType getType() {
        return ColumnType.STRING;
    }

    @Override
    public int numRows() {
        return values.length;
    }

    @Override
    public String get(int row) {
        return getString(row);
    }

    @Override
    public double getDouble(int row) {
        return Double.NaN;
    }

    @Override
    public String getString(int row) {
        int labelIndex = values[row];
        if(labelIndex < 0) {
            return null;
        } else {
            return labels[labelIndex];
        }
    }

    @Override
    public Date getDate(int row) {
        return null;
    }
}
