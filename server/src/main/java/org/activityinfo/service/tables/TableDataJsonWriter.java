package org.activityinfo.service.tables;

import com.google.gson.stream.JsonWriter;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.columns.ConstantColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Writes a table data {@code TableData} to JSON for consumption by the client
 */
public class TableDataJsonWriter {

    private final JsonWriter json;

    public TableDataJsonWriter(Writer json) {
        this.json = new JsonWriter(json);
    }

    public void write(TableData result) throws IOException {
        json.beginObject();
        json.name("rows");
        json.value(result.getNumRows());

        writeColumns(result);

        json.endObject();
        json.flush();
    }

    private void writeColumns(TableData result) throws IOException {
        json.name("columns");
        json.beginObject();
        for(Map.Entry<String, ColumnView> column : result.getColumns().entrySet()) {
            json.name(column.getKey());
            writeColumn(column.getValue());
        }
        json.endObject();
    }


    private void writeColumn(ColumnView view) throws IOException {
        json.beginObject();
        json.name("type");
        json.value(view.getType().name());

        if (view instanceof EmptyColumnView) {
            json.name("storage");
            json.value("empty");

        } else if (view instanceof ConstantColumnView) {
            writeConstantView(view);
        } else {
            writeArrayView(view);
        }
        json.endObject();
    }

    private void writeConstantView(ColumnView view) throws IOException {
        json.name("storage");
        json.value("constant");

        json.name("value");
        switch (view.getType()) {
            case STRING:
                json.value(view.getString(0));
                break;
            case NUMBER:
                json.value(view.getDouble(1));
                break;
            case DATE:
                throw new UnsupportedOperationException("todo");
        }
    }

    private void writeArrayView(ColumnView view) throws
            IOException {
        json.name("storage");
        json.value("array");

        json.name("values");
        json.beginArray();
        switch(view.getType()) {
            case STRING:
                writeStringValues(view);
                break;
            case NUMBER:
                writeDoubleValues(view);
                break;
            case DATE:
                writeDateValues(view);
                break;
        }
        json.endArray();
    }

    private void writeStringValues(ColumnView view) throws IOException {
        for(int i=0;i<view.numRows();++i) {
            json.value(view.getString(i));
        }
    }

    private void writeDoubleValues(ColumnView view) throws IOException {
        for(int i=0;i<view.numRows();++i) {
            json.value(view.getDouble(i));
        }
    }

    private void writeDateValues(ColumnView view) {
        throw new UnsupportedOperationException("todo");
    }

}
