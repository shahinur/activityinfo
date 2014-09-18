package org.activityinfo.model.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.activityinfo.model.resource.Record;

import java.io.IOException;

public class RecordSerializer extends JsonSerializer<Record> {
    @Override
    public void serialize(Record value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        RecordSerialization.writeProperties(jgen, value);
        jgen.writeEndObject();
    }
}
