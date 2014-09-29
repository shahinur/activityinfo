package org.activityinfo.model.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.RecordBeanClass;

import java.io.IOException;

public class RecordBeanDeserializer <T> extends JsonDeserializer<T> {

    private RecordBeanClass<T> beanClass;

    public RecordBeanDeserializer(RecordBeanClass<T> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Record record = RecordSerialization.readRecord(jp);
        return beanClass.toBean(record);
    }
}
