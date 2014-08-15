package org.activityinfo.model.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.activityinfo.model.resource.*;

import java.io.IOException;
import java.util.List;

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

            if(propertyName.equals("@id")) {
                resource.setId(ResourceId.valueOf(reader.getText()));
            } else if(propertyName.equals("@owner")) {
                resource.setOwnerId(ResourceId.valueOf(reader.getText()));
            } else if(propertyName.equals("@version")) {
                resource.setVersion(reader.getNumberValue().longValue());
            } else {
                readProperty(reader, resource, propertyName);
            }
        }
        return resource;

    }

    private void readProperty(JsonParser reader, PropertyBag<?> resource, String propertyName) throws IOException {
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



    private Record readRecord(JsonParser reader) throws IOException {
        Record record = new Record();
        while(reader.nextToken() == JsonToken.FIELD_NAME) {
            String propertyName = reader.getCurrentName();

            if(reader.nextToken() != JsonToken.VALUE_NULL) {
                readProperty(reader, record, propertyName);
            }
        }
        return record;
    }

    private List<Object> readArray(JsonParser reader) throws IOException {
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
