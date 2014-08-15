package org.activityinfo.model.json;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.activityinfo.model.resource.PropertyBag;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class ResourceSerializer extends JsonSerializer<Resource> {

    @Override
    public void serialize(Resource resource,
                          JsonGenerator json,
                          SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

        json.writeStartObject();
        json.writeStringField("@id", resource.getId().asString());
        json.writeStringField("@owner", resource.getOwnerId().asString());
        json.writeNumberField("@version", resource.getVersion());

        writeProperties(json, resource);

        json.writeEndObject();

    }

    private void writeProperties(JsonGenerator json, PropertyBag<?> resource) throws IOException {
        for (Map.Entry<String, Object> entry : resource.getProperties().entrySet()) {
            json.writeFieldName(entry.getKey());
            writeValue(json, entry.getValue());
        }
    }

    private void writeValue(JsonGenerator json, Object value) throws IOException {

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

    private void writeArray(JsonGenerator json, Collection collection) throws IOException {
        json.writeStartArray();
        for(Object item : collection) {
            writeValue(json, item);
        }
        json.writeEndArray();
    }
}
