package org.activityinfo.ui.store.remote.client.table;

import com.google.gwt.core.client.JavaScriptObject;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.util.Date;

public class JsDoubleArrayColumnView implements ColumnView {

    private int numRows;
    private JavaScriptObject array;

    public JsDoubleArrayColumnView(int numRows, JavaScriptObject array) {
        this.numRows = numRows;
        this.array = array;
    }

    @Override
    public ColumnType getType() {
        return ColumnType.NUMBER;
    }

    @Override
    public int numRows() {
        return numRows;
    }

    @Override
    public Object get(int row) {
        return getDouble(row);
    }

    @Override
    public double getDouble(int row) {
        return getDouble(array, row);
    }

    private native double getDouble(JavaScriptObject array, int row) /*-{
      return +array[row];
    }-*/;

    @Override
    public String getString(int row) {
        return null;
    }

    @Override
    public Date getDate(int row) {
        return null;
    }
}
