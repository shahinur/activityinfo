package org.activityinfo.service.tables.function;

import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.util.Date;

public class BooleanBinaryOp implements ColumnView {

    private ColumnView x;
    private ColumnView y;

    public BooleanBinaryOp(ColumnView x, ColumnView y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public ColumnType getType() {
        return ColumnType.BOOLEAN;
    }

    @Override
    public int numRows() {
        return x.numRows();
    }

    @Override
    public Object get(int row) {
        return getBoolean(row);
    }

    @Override
    public double getDouble(int row) {
        return 0;
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
        int bx = x.getBoolean(row);
        int by = y.getBoolean(row);
        if(bx == NA || by == NA) {
            return NA;
        } else if(bx == TRUE && by == TRUE) {
            return TRUE;
        } else {
            return FALSE;
        }
    }
}
