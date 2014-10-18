package org.activityinfo.client.table;

import com.google.common.base.Function;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import org.activityinfo.model.table.ColumnSet;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.views.ConstantColumnView;
import org.activityinfo.model.table.views.EmptyColumnView;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a TableData and a set of ColumnViews that wrap a set of JavaScript arrays.
 */
public class JsTableDataBuilder implements Function<Response, ColumnSet> {


    @Override
    public ColumnSet apply(Response response) {
        return build(response.getText());
    }

    public ColumnSet build(String json) {
        Map<String, ColumnView> columnMap = new HashMap<>();

        TableDataOverlay overlay = JsonUtils.safeEval(json);
        JsArrayString columns = overlay.getColumns();
        for(int i=0;i!=columns.length();++i) {
            String columnId = columns.get(i);
            columnMap.put(columnId, createView(overlay.getNumRows(), overlay.getColumn(columnId)));
        }
        return new ColumnSet(overlay.getNumRows(), columnMap);
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
            case DATE:
                return createDateColumn(numRows);
            default:
                throw new UnsupportedOperationException(overlay.getTypeName());
        }
    }

    private ColumnView createArrayView(int numRows, ColumnViewOverlay overlay) {
        switch (overlay.getType()) {
            case STRING:
                return new JsStringColumnArrayView(numRows, overlay.getArray().<JsArrayString>cast());
            case NUMBER:
                return new JsDoubleArrayColumnView(numRows, overlay.getArray().<JsArrayString>cast());
            case DATE:
                return createDateColumn(numRows);
            default:
                throw new UnsupportedOperationException();
        }
    }

    private ColumnView createDateColumn(int numRows) {
        return new EmptyColumnView(numRows, ColumnType.DATE);
    }
}
