package org.activityinfo.model.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.activityinfo.model.resource.IsRecord;

import java.io.IOException;

public class IsRecordSerializer extends JsonSerializer<IsRecord> {
    @Override
    public void serialize(IsRecord object,
                          JsonGenerator json,
                          SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

        json.writeStartObject();
        RecordSerialization.writeProperties(json, object.asRecord());
        json.writeEndObject();
    }
}
