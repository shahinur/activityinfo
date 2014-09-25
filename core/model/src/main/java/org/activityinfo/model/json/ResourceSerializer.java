package org.activityinfo.model.json;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.activityinfo.model.resource.Resource;

import java.io.IOException;

public class ResourceSerializer extends JsonSerializer<Resource> {

    @Override
    public void serialize(Resource resource,
                          JsonGenerator json,
                          SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

        json.writeStartObject();
        json.writeStringField("@id", resource.getId().asString());
        json.writeStringField("@owner", resource.getOwnerId().asString());
        json.writeNumberField("@version", resource.getVersion());

        RecordSerialization.writeProperties(json, resource.getValue());

        json.writeEndObject();
    }

}
