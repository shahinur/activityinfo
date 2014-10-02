package org.activityinfo.model.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.activityinfo.model.record.RecordBeanClass;

import java.io.IOException;

public class RecordBeanSerializer<T> extends JsonSerializer<T> {

    private final RecordBeanClass<T> recordBeanClass;

    public RecordBeanSerializer(RecordBeanClass<T> recordBeanClass) {
        this.recordBeanClass = recordBeanClass;
    }

    @Override
    public void serialize(T value, JsonGenerator json, SerializerProvider provider) throws IOException, JsonProcessingException {
        json.writeStartObject();
        RecordSerialization.writeProperties(json, recordBeanClass.toRecord(value));
        json.writeEndObject();
    }
}
