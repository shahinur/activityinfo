package org.activityinfo.ui.client.service.table;

import com.google.common.collect.Maps;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.table.*;
import org.activityinfo.model.table.columns.ConstantColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;
import org.activityinfo.promise.Promise;

import java.util.Map;

public class RemoteJsonTableService implements TableServiceAsync {

    @Override
    public Promise<TableData> query(TableModel tableModel) {

        // Do a first check of the TableModel before firing
        if(tableModel.getRowSources().isEmpty()) {
            return Promise.rejected(new IllegalArgumentException("No rowSources specified."));
        }

        final Promise<TableData> result = new Promise<>();
        RequestBuilder request = new RequestBuilder(RequestBuilder.POST, "/service/table");
        request.setRequestData(Resources.toJson(tableModel.asRecord()));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        request.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                if(response.getStatusCode() != 200) {
                    result.onFailure(new RuntimeException("Status code: " + response.getStatusCode()));
                } else {
                    TableData data;
                    try {
                        data = parseResult(response);
                    } catch(Exception e) {
                        result.reject(e);
                        return;
                    }
                    result.onSuccess(data);
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                result.onFailure(exception);
            }
        });
        try {
            request.send();
        } catch (RequestException e) {
            result.reject(e);
        }

        return result;
    }

    private TableData parseResult(Response response) {
        JSONObject tableData = JSONParser.parseLenient(response.getText()).isObject();

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
