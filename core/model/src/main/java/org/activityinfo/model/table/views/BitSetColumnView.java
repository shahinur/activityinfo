package org.activityinfo.model.table.views;

import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.util.BitSet;
import java.util.Date;

public class BitSetColumnView implements ColumnView {
    private int numRows;
    private BitSet values;
    private BitSet missing;

    public BitSetColumnView(int numRows, BitSet values, BitSet missing) {
        this.numRows = numRows;
        this.values = values;
        this.missing = missing;
    }


    @Override
    public ColumnType getType() {
        return ColumnType.BOOLEAN;
    }

    @Override
    public int numRows() {
        return numRows;
    }

    @Override
    public Object get(int row) {
        return getBoolean(row);
    }

    @Override
    public double getDouble(int row) {
        if(missing.get(row)) {
            return Double.NaN;
        } else {
            return values.get(row) ? 1 : 0;
        }
    }

    @Override
    public String getString(int row) {
        return null;
    }

    @Override
    public Date getDate(int row) {
        return null;
    }

    @Override
    public int getBoolean(int row) {
        if(missing.get(row)) {
            return NA;
        } else {
            return values.get(row) ? 1 : 0;
        }
    }
}
