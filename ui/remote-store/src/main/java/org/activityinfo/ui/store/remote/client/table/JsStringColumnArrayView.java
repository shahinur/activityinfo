package org.activityinfo.ui.store.remote.client.table;

import com.google.gwt.json.client.JSONArray;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.util.Date;

public class JsStringColumnArrayView implements ColumnView {

    private final int numRows;
    private final JsStringColumnArray array;

    public JsStringColumnArrayView(int numRows, JSONArray array) {
        this.numRows = numRows;
        this.array = array.getJavaScriptObject().cast();
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
        return array.getString(row);
    }

    @Override
    public double getDouble(int row) {
        return array.getNumber(row);
    }

    @Override
    public String getString(int row) {
        return array.getString(row);
    }

    @Override
    public Date getDate(int row) {
        return null;
    }
}
