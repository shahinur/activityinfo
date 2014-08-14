package org.activityinfo.model.table.columns;

import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.util.Date;

public class ConstantColumnView implements ColumnView {

    private final ColumnType type;
    private final double doubleValue;
    private final String stringValue;
    private final int numRows;

    public ConstantColumnView(String value, int numRows) {
        this.type = ColumnType.STRING;
        this.doubleValue = Double.NaN;
        this.stringValue = value;
        this.numRows = numRows;
    }

    @Override
    public ColumnType getType() {
        return type;
    }

    @Override
    public int numRows() {
        return numRows;
    }

    @Override
    public Object get(int row) {
        return stringValue;
    }

    @Override
    public double getDouble(int row) {
        return doubleValue;
    }

    @Override
    public String getString(int row) {
       return stringValue;
    }

    @Override
    public Date getDate(int row) {
        return null;
    }
}
