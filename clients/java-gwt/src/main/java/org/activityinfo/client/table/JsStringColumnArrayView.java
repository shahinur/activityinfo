package org.activityinfo.client.table;

import com.google.gwt.core.client.JsArrayString;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.util.Date;

public class JsStringColumnArrayView implements ColumnView {

    private final int numRows;
    private final JsArrayString array;

    public JsStringColumnArrayView(int numRows, JsArrayString array) {
        this.numRows = numRows;
        this.array = array;
    }

    @Override
    public ColumnType getType() {
        return ColumnType.STRING;
    }

    @Override
    public int numRows() {
        return numRows;
    }

    @Override
    public Object get(int row) {
        return array.get(row);
    }

    @Override
    public double getDouble(int row) {
        return Double.NaN;
    }

    @Override
    public String getString(int row) {
        return array.get(row);
    }

    @Override
    public Date getDate(int row) {
        return null;
    }

    @Override
    public int getBoolean(int row) {
        return NA;
    }
}
