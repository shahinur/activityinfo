package org.activityinfo.ui.store.remote.client.table;

import com.google.common.base.Function;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.columns.ConstantColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a TableData and a set of ColumnViews that wrap a set of JavaScript arrays.
 */
public class JsTableDataBuilder implements Function<String, TableData> {


    @Override
    public TableData apply(String input) {

        Map<String, ColumnView> columnMap = new HashMap<>();

        TableDataOverlay overlay = JsonUtils.safeEval(input);
        JsArrayString columns = overlay.getColumns();
        for(int i=0;i!=columns.length();++i) {
            String columnId = columns.get(i);
            columnMap.put(columnId, createView(overlay.getNumRows(), overlay.getColumn(columnId)));
        }
        return new TableData(overlay.getNumRows(), columnMap);
    }

    private ColumnView createView(int numRows, ColumnViewOverlay column) {
        switch(column.getStorageMode()) {
            case "empty":
                return new EmptyColumnView(numRows, column.getType());
            case "constant":
                return createConstantView(numRows, column);
            case "array":
                return createArrayView(numRows, column);
        }
        throw new UnsupportedOperationException(column.getStorageMode());
    }

    private ColumnView createConstantView(int numRows, ColumnViewOverlay overlay) {
        switch (overlay.getType()) {
            case STRING:
                return new ConstantColumnView(numRows, overlay.getStringValue());
            case NUMBER:
                return new ConstantColumnView(numRows, overlay.getDoubleValue());
            default:
            case DATE:
                throw new UnsupportedOperationException(overlay.getTypeName());
        }
    }

    private ColumnView createArrayView(int numRows, ColumnViewOverlay overlay) {
        switch (overlay.getType()) {
            case STRING:
                return new JsStringColumnArrayView(numRows, overlay.getArray().<JsArrayString>cast());
            case NUMBER:
                return new JsDoubleArrayColumnView(numRows, overlay.getArray().<JsArrayString>cast());
            default:
            case DATE:
                throw new UnsupportedOperationException();
        }
    }
}
