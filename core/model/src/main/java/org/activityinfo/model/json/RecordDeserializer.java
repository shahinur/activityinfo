package org.activityinfo.model.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.activityinfo.model.record.Record;

import java.io.IOException;

public class RecordDeserializer extends JsonDeserializer<Record> {
    @Override
    public Record deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return RecordSerialization.readRecord(jp);
    }
}
