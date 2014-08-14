package org.activityinfo.service.writer;

import com.google.common.base.Charsets;
import com.google.gson.stream.JsonWriter;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.columns.ConstantColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Writes a table data {@code TableData} to JSON for consumption by the client
 */
public class TableDataJsonWriter implements MessageBodyWriter<TableData> {


    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(TableData.class);
    }

    @Override
    public long getSize(TableData tableData,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(TableData tableData,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {

        JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(entityStream, Charsets.UTF_8));
        write(jsonWriter, tableData);
        jsonWriter.flush();
    }

    public void write(JsonWriter json, TableData result) throws IOException {
        json.beginObject();
        json.name("rows");
        json.value(result.getNumRows());

        writeColumns(json, result);

        json.endObject();
        json.flush();
    }

    private void writeColumns(JsonWriter json, TableData result) throws IOException {
        json.name("columns");
        json.beginObject();
        for(Map.Entry<String, ColumnView> column : result.getColumns().entrySet()) {
            json.name(column.getKey());
            writeColumn(json, column.getValue());
        }
        json.endObject();
    }


    private void writeColumn(JsonWriter json, ColumnView view) throws IOException {
        json.beginObject();
        json.name("type");
        json.value(view.getType().name());

        if (view instanceof EmptyColumnView) {
            json.name("storage");
            json.value("empty");

        } else if (view instanceof ConstantColumnView) {
            writeConstantView(json, view);
        } else {
            writeArrayView(json, view);
        }
        json.endObject();
    }

    private void writeConstantView(JsonWriter json, ColumnView view) throws IOException {
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

    private void writeArrayView(JsonWriter json, ColumnView view) throws
            IOException {
        json.name("storage");
        json.value("array");

        json.name("values");
        json.beginArray();
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
        json.endArray();
    }

    private void writeStringValues(JsonWriter json, ColumnView view) throws IOException {
        for(int i=0;i<view.numRows();++i) {
            json.value(view.getString(i));
        }
    }

    private void writeDoubleValues(JsonWriter json, ColumnView view) throws IOException {
        for(int i=0;i<view.numRows();++i) {
            json.value(view.getDouble(i));
        }
    }

    private void writeDateValues(JsonWriter json, ColumnView view) {
        throw new UnsupportedOperationException("todo");
    }

}
