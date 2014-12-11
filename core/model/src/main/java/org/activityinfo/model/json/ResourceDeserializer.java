package org.activityinfo.model.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Preconditions;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;

import java.io.IOException;

public class ResourceDeserializer extends JsonDeserializer<Resource> {

    @Override
    public Resource deserialize(JsonParser reader,
                                DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return deserialize(reader);
    }

    public static Resource deserialize(JsonParser reader) throws IOException {
        Preconditions.checkState(reader.getCurrentToken() == JsonToken.START_OBJECT);

        RecordBuilder recordBuilder = Records.builder();
        ResourceId id = null;
        ResourceId ownerId = null;
        long version = 0L;

        while(reader.nextToken() == JsonToken.FIELD_NAME) {
            String propertyName = reader.getCurrentName();

            // read the value
            reader.nextToken();

            if (propertyName.equals("@id")) {
                id = ResourceId.valueOf(reader.getText());

            } else if (propertyName.equals("@owner")) {
                ownerId = ResourceId.valueOf(reader.getText());

            } else if (propertyName.equals("@version")) {
                if(reader.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
                    version = reader.getNumberValue().longValue();
                } else if(reader.getCurrentToken() == JsonToken.VALUE_STRING) {
                    version = Long.parseLong(reader.getValueAsString());
                } else {
                    throw new UnsupportedOperationException("@version = " + reader.getCurrentToken().name());
                }

            } else {
                RecordSerialization.readProperty(reader, recordBuilder, propertyName);
            }
        }

        return Resources.createResource(recordBuilder).setId(id).setOwnerId(ownerId).setVersion(version);
    }
}
