package org.activityinfo.model.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.columns.ConstantColumnView;
import org.activityinfo.model.table.columns.DoubleArrayColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;
import org.activityinfo.model.table.columns.StringArrayColumnView;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class TableDataDeserializer extends JsonDeserializer<TableData> {


    @Override
    public TableData deserialize(JsonParser reader,
                                  DeserializationContext ctxt) throws IOException {

        TreeNode tree = reader.readValueAsTree();
        int rows = ((NumericNode)tree.get("rows")).intValue();

        Map<String, ColumnView> columnMap = Maps.newHashMap();
        ObjectNode columns = (ObjectNode) tree.get("columns");
        Iterator<String> fieldNames = columns.fieldNames();

        while(fieldNames.hasNext()) {
            String columnId = fieldNames.next();
            ColumnView view = readView(rows, (ObjectNode) columns.get(columnId));
            columnMap.put(columnId, view);
        }
        return new TableData(rows, columnMap);
    }

    private ColumnView readView(int rows, ObjectNode column) {

        ColumnType type = ColumnType.valueOf(column.get("type").asText());
        String storage = column.get("storage").asText();

        switch(storage) {
            case "empty":
                return new EmptyColumnView(rows, type);
            case "constant":
                return readConstantView(column, type, rows);
            case "array":
                return readArrayView(column, type);

            default:
                throw new UnsupportedOperationException("Unrecognized storage mode: " + storage);
        }
    }

    private ColumnView readConstantView(ObjectNode column, ColumnType type, int rows) {
        switch(type) {
            case STRING:
                return new ConstantColumnView(rows, column.get("value").asText());
            case NUMBER:
                return new ConstantColumnView(rows, column.get("value").asDouble());
            default:
                throw new UnsupportedOperationException();
        }
    }

    private ColumnView readArrayView(ObjectNode column, ColumnType type) {
        switch(type) {
            case STRING:
                return readStringArray(column);
            case NUMBER:
                return readDoubleArray(column);
            default:
                throw new UnsupportedOperationException();
        }
    }

    private ColumnView readStringArray(ObjectNode column) {
        ArrayNode arrayNode = (ArrayNode) column.get("values");
        String [] array = new String[arrayNode.size()];

        for(int i=0;i!=array.length;++i) {
            array[i] = arrayNode.get(i).asText();
        }
        return new StringArrayColumnView(array);
    }


    private ColumnView readDoubleArray(ObjectNode column) {
        ArrayNode arrayNode = (ArrayNode) column.get("values");
        double [] array = new double[arrayNode.size()];

        for(int i=0;i!=array.length;++i) {
            JsonNode jsonNode = arrayNode.get(i);
            array[i] = jsonNode.asDouble(Double.NaN);
        }
        return new DoubleArrayColumnView(array);
    }
}
