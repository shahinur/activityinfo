package org.activityinfo.io.load.format;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activityinfo.model.json.ObjectMapperFactory;

import java.io.FileWriter;
import java.io.IOException;

public class JsonWriter implements AutoCloseable {

    private final JsonGenerator json;

    public JsonWriter() throws IOException {

        ObjectMapper objectMapper = ObjectMapperFactory.get();
        FileWriter writer = new FileWriter("/home/alex/dev/activityinfo3/store/testing/src/main/resources/lcca-survey.json");
        json = objectMapper.getFactory().createGenerator(writer).useDefaultPrettyPrinter();
        json.writeStartArray();
    }

    @Override
    public void close() throws Exception {
        json.writeEndArray();
        json.close();
    }

}
