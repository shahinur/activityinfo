package org.activityinfo.model.table.views;

import com.google.gwt.core.shared.GwtIncompatible;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.util.Date;

@GwtIncompatible("java.util.BitSet")
@SuppressWarnings("NonJREEmulationClassesInClientCode")
public class BitSetColumnView implements ColumnView {
    private int numRows;
    private java.util.BitSet values;
    private java.util.BitSet missing;

    public BitSetColumnView(int numRows, java.util.BitSet values, java.util.BitSet missing) {
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
