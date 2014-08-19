package org.activityinfo.model.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.columns.ConstantColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;

import java.io.IOException;
import java.util.Map;

public class TableDataSerializer extends JsonSerializer<TableData> {

    @Override
    public void serialize(TableData result,
                          JsonGenerator json,
                          SerializerProvider provider) throws IOException {

        json.writeStartObject();
        json.writeNumberField("rows", result.getNumRows());

        writeColumns(json, result);

        json.writeEndObject();
        json.flush();
    }

    private void writeColumns(JsonGenerator json, TableData result) throws IOException {
        json.writeObjectFieldStart("columns");
        for(Map.Entry<String, ColumnView> column : result.getColumns().entrySet()) {
            json.writeFieldName(column.getKey());
            writeColumn(json, column.getValue());
        }
        json.writeEndObject();
    }


    private void writeColumn(JsonGenerator json, ColumnView view) throws IOException {
        json.writeStartObject();
        json.writeStringField("type", view.getType().name());

        if (view instanceof EmptyColumnView) {
            json.writeStringField("storage", "empty");

        } else if (view instanceof ConstantColumnView) {
            writeConstantView(json, view);

        } else {
            writeArrayView(json, view);
        }
        json.writeEndObject();
    }

    private void writeConstantView(JsonGenerator json, ColumnView view) throws IOException {
        json.writeStringField("storage", "constant");

        switch (view.getType()) {
            case STRING:
                json.writeStringField("value", view.getString(0));
                break;
            case NUMBER:
                json.writeNumberField("value", view.getDouble(0));
                break;
            case DATE:
                throw new UnsupportedOperationException("todo");
        }
    }

    private void writeArrayView(JsonGenerator json, ColumnView view) throws
            IOException {
        json.writeStringField("storage", "array");
        json.writeFieldName("values");
        json.writeStartArray(view.numRows());
        switch(view.getType()) {
            case STRING:
                writeStringValues(json, view);
                break;
            case NUMBER:
                writeDoubleValues(json, view);
                break;
            case DATE:
                writeDateValues(json, view);
                break;
        }
        json.writeEndArray();
    }

    private void writeStringValues(JsonGenerator json, ColumnView view) throws IOException {
        for(int i=0;i<view.numRows();++i) {
            json.writeString(view.getString(i));
        }
    }

    private void writeDoubleValues(JsonGenerator json, ColumnView view) throws IOException {
        for(int i=0;i<view.numRows();++i) {
            json.writeNumber(view.getDouble(i));
        }
    }

    private void writeDateValues(JsonGenerator json, ColumnView view) {
        throw new UnsupportedOperationException("todo");
    }

}
