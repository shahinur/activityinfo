package org.activityinfo.service.tables.function;

import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.util.Date;

public class DoubleBinaryOpView implements ColumnView {

    private DoubleBinaryOp op;
    private ColumnView x;
    private ColumnView y;

    public DoubleBinaryOpView(DoubleBinaryOp op, ColumnView x, ColumnView y) {
        this.op = op;
        this.x = x;
        this.y = y;
    }

    @Override
    public ColumnType getType() {
        return ColumnType.NUMBER;
    }

    @Override
    public int numRows() {
        return x.numRows();
    }

    @Override
    public Object get(int row) {
        return getDouble(row);
    }

    @Override
    public double getDouble(int row) {
        return op.apply(x.getDouble(row), y.getDouble(row));
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
        return 0;
    }
}
