package org.activityinfo.io.load.excel;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.service.store.ImportWriter;
import org.activityinfo.service.store.InstanceWriter;

import java.io.FileWriter;
import java.io.IOException;

public class JsonWriter implements ImportWriter, AutoCloseable {

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

    @Override
    public InstanceWriter createFormClass(FormClass formClass) {
        try {
            json.writeObject(formClass.asResource());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new InstanceWriter() {
            @Override
            public void write(FormInstance instance) {
                try {
                    json.writeObject(instance.asResource());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
