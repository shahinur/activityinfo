package org.activityinfo.model.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Preconditions;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;

import java.io.IOException;

public class ResourceDeserializer extends JsonDeserializer<Resource> {
    @Override
    public Resource deserialize(JsonParser reader,
                                DeserializationContext deserializationContext) throws IOException, JsonProcessingException {


        Preconditions.checkState(reader.getCurrentToken() == JsonToken.START_OBJECT);

        Resource resource = Resources.createResource();

        while(reader.nextToken() == JsonToken.FIELD_NAME) {
            String propertyName = reader.getCurrentName();

            // read the value
            reader.nextToken();

            if (propertyName.equals("@id")) {
                resource.setId(ResourceId.valueOf(reader.getText()));

            } else if (propertyName.equals("@owner")) {
                resource.setOwnerId(ResourceId.valueOf(reader.getText()));

            } else if (propertyName.equals("@version")) {
                resource.setVersion(reader.getNumberValue().longValue());

            } else {
                RecordSerialization.readProperty(reader, resource, propertyName);
            }
        }
        return resource;
    }

}
