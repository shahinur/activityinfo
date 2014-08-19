package org.activityinfo.model.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.collect.Lists;
import org.activityinfo.model.resource.PropertyBag;
import org.activityinfo.model.resource.Record;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RecordSerialization {
    static void writeProperties(JsonGenerator json, PropertyBag<?> resource) throws IOException {
        for (Map.Entry<String, Object> entry : resource.getProperties().entrySet()) {
            json.writeFieldName(entry.getKey());
            writeValue(json, entry.getValue());
        }
    }

    private static void writeValue(JsonGenerator json, Object value) throws IOException {

        if(value instanceof String) {
            json.writeString((String) value);

        } else if(value instanceof Number) {
            json.writeNumber(((Number) value).doubleValue());

        } else if(value instanceof Boolean) {
            json.writeBoolean(value == Boolean.TRUE);

        } else if(value instanceof Record) {
            json.writeStartObject();
            writeProperties(json, (Record) value);
            json.writeEndObject();

        } else if(value instanceof Collection) {
            writeArray(json, (Collection)value);
        }
    }

    private static void writeArray(JsonGenerator json, Collection collection) throws IOException {
        json.writeStartArray();
        for(Object item : collection) {
            writeValue(json, item);
        }
        json.writeEndArray();
    }

    public static void readProperty(JsonParser reader, PropertyBag<?> resource, String propertyName) throws IOException {
        if(reader.getCurrentToken() == JsonToken.VALUE_STRING) {
            resource.set(propertyName, reader.getText());
        } else if(reader.getCurrentToken() == JsonToken.VALUE_NUMBER_INT ||
                  reader.getCurrentToken() == JsonToken.VALUE_NUMBER_FLOAT) {

            resource.set(propertyName, reader.getNumberValue().doubleValue());

        } else if(reader.getCurrentToken() == JsonToken.VALUE_TRUE) {
            resource.set(propertyName, true);
        } else if(reader.getCurrentToken() == JsonToken.VALUE_FALSE) {
            resource.set(propertyName, false);
        } else if(reader.getCurrentToken() == JsonToken.VALUE_NULL) {
            // noop
        } else if(reader.getCurrentToken() == JsonToken.START_OBJECT) {
            resource.set(propertyName, readRecord(reader));

        } else if(reader.getCurrentToken() == JsonToken.START_ARRAY) {
            resource.set(propertyName, readArray(reader));
        }
    }

    public static Record readRecord(JsonParser reader) throws IOException {
        Record record = new Record();
        while(reader.nextToken() == JsonToken.FIELD_NAME) {
            String propertyName = reader.getCurrentName();

            if(reader.nextToken() != JsonToken.VALUE_NULL) {
                readProperty(reader, record, propertyName);
            }
        }
        return record;
    }

    public static List<Object> readArray(JsonParser reader) throws IOException {
        List<Object> array = Lists.newArrayList();
        while(reader.nextToken() != JsonToken.END_ARRAY) {

            if(reader.getCurrentToken() == JsonToken.VALUE_STRING) {
                array.add(reader.getText());

            } else if(reader.getCurrentToken() == JsonToken.VALUE_NUMBER_INT ||
                      reader.getCurrentToken() == JsonToken.VALUE_NUMBER_FLOAT) {

                array.add(reader.getDoubleValue());

            } else if(reader.getCurrentToken() == JsonToken.VALUE_TRUE) {

                array.add(true);

            } else if(reader.getCurrentToken() == JsonToken.VALUE_FALSE) {
                array.add(false);

            } else if(reader.getCurrentToken() == JsonToken.VALUE_NULL) {

            } else if(reader.getCurrentToken() == JsonToken.START_OBJECT) {
                array.add(readRecord(reader));

            } else if(reader.getCurrentToken() == JsonToken.START_ARRAY) {
                array.add(readArray(reader));
            }
        }
        return array;
    }
}
