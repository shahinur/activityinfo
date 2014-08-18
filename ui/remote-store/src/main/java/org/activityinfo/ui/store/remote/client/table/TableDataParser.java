package org.activityinfo.ui.store.remote.client.table;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.columns.ConstantColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;

import java.util.Map;

public class TableDataParser implements Function<Response, TableData> {


    @Override
    public TableData apply(Response input) {
        JSONObject tableData = JSONParser.parseLenient(input.getStatusText()).isObject();

        int rowCount = (int)tableData.get("rows").isNumber().doubleValue();
        JSONObject columns = tableData.get("columns").isObject();

        Map<String, ColumnView> columnMap = Maps.newHashMap();
        for(String columnId : columns.keySet()) {
            columnMap.put(columnId, createView(columns.get(columnId).isObject(), rowCount));
        }
        return new TableData(rowCount, columnMap);
    }

    private ColumnView createView(JSONObject object, int rowCount) {
        ColumnType type = ColumnType.valueOf(object.get("type").isString().stringValue());
        String storage = object.get("storage").isString().stringValue();
        switch(storage) {
            case "empty":
                return new EmptyColumnView(type, rowCount);
            case "constant":
                return createConstantView(type, rowCount, object.get("value"));
            case "array":
                return createArrayView(type, rowCount, object.get("values"));
            default:
                throw new UnsupportedOperationException("Unsupported storage: " + storage);
        }
    }

    private ColumnView createArrayView(ColumnType type, int rowCount, JSONValue array) {
        switch(type) {
            case STRING:
                return new JsStringColumnArrayView(rowCount, array.isArray());
            case NUMBER:
            case DATE:
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    private ColumnView createConstantView(ColumnType type, int rowCount, JSONValue value) {
        switch(type) {
            case STRING:
                return new ConstantColumnView(value.isString().stringValue(), rowCount);
            case NUMBER:
            case DATE:
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }
}
