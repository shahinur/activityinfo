package org.activityinfo.model.table.summary;

import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.util.Date;
import java.util.Set;

public class UniqueValueColumnView implements ColumnView {

    private ColumnType type;
    private ColumnView[] columns;

    private static final double EPSILON = Double.MIN_VALUE;

    public UniqueValueColumnView(Set<ColumnView> columnViews) {
        int columnIndex = 0;
        for(ColumnView columnView : columnViews) {
            columns[columnIndex] = columnView;
            columnIndex++;
        }
        type = columns[0].getType();
    }

    @Override
    public ColumnType getType() {
        return type;
    }

    @Override
    public int numRows() {
        return columns[0].numRows();
    }

    @Override
    public Object get(int row) {
        switch(type) {
            case STRING:
                return getString(row);
            case NUMBER:
                return getDouble(row);
            case DATE:
                return getDate(row);
        }
        throw new IllegalStateException("type = " + type);
    }

    @Override
    public double getDouble(int row) {
        double uniqueValue = Double.NaN;
        for(int i=0;i!=columns.length;++i) {
            double value = columns[i].getDouble(row);
            if(!Double.isNaN(value)) {
                if(Double.isNaN(uniqueValue)) {
                    uniqueValue = value;
                } else if(Math.abs(value - uniqueValue) > EPSILON) {
                    return Double.NaN;
                }
            }
        }
        return uniqueValue;
    }

    @Override
    public String getString(int row) {
        String uniqueValue = null;
        for(int i=0;i!=columns.length;++i) {
            String value = columns[i].getString(row);
            if(value != null) {
                if(uniqueValue == null) {
                    uniqueValue = value;
                } else if(!uniqueValue.equals(value)) {
                    return null;
                }
            }
        }
        return uniqueValue;
    }

    @Override
    public Date getDate(int row) {
        throw new UnsupportedOperationException("todo");
    }
}
