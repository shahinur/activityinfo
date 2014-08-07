package org.activityinfo.service.tables;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.columns.ConstantColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;
import org.activityinfo.model.table.columns.StringArrayColumnView;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class TableDataJsonWriterTest {


    @Test
    public void test() throws IOException {

        Map<String, ColumnView> columns = Maps.newHashMap();
        columns.put("a", new StringArrayColumnView(Arrays.asList("x", "y", "z")));
        columns.put("b", new ConstantColumnView("hello", 3));
        columns.put("c", new EmptyColumnView(ColumnType.STRING, 3));

        TableData tableData = new TableData(3, columns);

        StringWriter stringWriter = new StringWriter();
        TableDataJsonWriter writer = new TableDataJsonWriter(stringWriter);
        writer.write(tableData);
        System.out.println(stringWriter.toString());

        String expectedJson = "{\"rows\":3,\"columns\":{\"b\":{\"type\":\"STRING\",\"storage\":\"constant\"," +
                          "\"value\":\"hello\"},\"c\":{\"type\":\"STRING\",\"storage\":\"empty\"}," +
                          "\"a\":{\"type\":\"STRING\",\"storage\":\"array\",\"values\":[\"x\",\"y\",\"z\"]}}}";

        JsonParser jsonParser = new JsonParser();
        JsonElement expected = jsonParser.parse(expectedJson);
        JsonElement actual = jsonParser.parse(stringWriter.toString());

        assertThat(actual, equalTo(expected));

    }
}