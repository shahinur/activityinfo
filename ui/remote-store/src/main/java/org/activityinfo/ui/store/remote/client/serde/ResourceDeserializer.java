package org.activityinfo.ui.store.remote.client.serde;

import com.github.nmorel.gwtjackson.client.JsonDeserializationContext;
import com.github.nmorel.gwtjackson.client.JsonDeserializer;
import com.github.nmorel.gwtjackson.client.JsonDeserializerParameters;
import com.github.nmorel.gwtjackson.client.stream.JsonReader;
import com.github.nmorel.gwtjackson.client.stream.JsonToken;
import com.google.common.collect.Lists;
import org.activityinfo.model.resource.*;

import java.util.List;

public class ResourceDeserializer extends JsonDeserializer<Resource> {

    @Override
    protected Resource doDeserialize(JsonReader reader,
                                     JsonDeserializationContext ctx,
                                     JsonDeserializerParameters params) {

        Resource resource = Resources.createResource();

        reader.beginObject();
        while(reader.peek() != JsonToken.END_OBJECT) {
            String propertyName = reader.nextName();
            if(propertyName.equals("@id")) {
                resource.setId(ResourceId.valueOf(reader.nextString()));
            } else if(propertyName.equals("@owner")) {
                resource.setOwnerId(ResourceId.valueOf(reader.nextString()));
            } else if(propertyName.equals("@version")) {
                resource.setVersion(reader.nextLong());
            } else {
                resource.set(propertyName, readPropertyValue(reader));
            }
        }
        reader.endObject();
        return resource;
    }

    private Record readRecord(JsonReader reader) {
        Record record = new Record();
        readProperties(reader, record);
        return record;
    }

    private void readProperties(JsonReader reader, PropertyBag propertyBag) {
        reader.beginObject();
        while(reader.peek() != JsonToken.END_OBJECT) {
            String name = reader.nextName();
            Object value = readPropertyValue(reader);
            propertyBag.set(name, value);
        }
        reader.endObject();
    }

    private Object readArray(JsonReader reader) {
        List<Object> array = Lists.newArrayList();
        reader.beginArray();
        while(reader.peek() != JsonToken.END_ARRAY) {
            array.add(readPropertyValue(reader));
        }
        reader.endArray();
        return array;
    }

    private Object readPropertyValue(JsonReader reader) {
        JsonToken next = reader.peek();
        switch(next) {
            case STRING:
                return reader.nextString();

            case BEGIN_ARRAY:
                return readArray(reader);

            case BEGIN_OBJECT:
                return readRecord(reader);
            case NUMBER:
                return reader.nextDouble();

            case BOOLEAN:
                return reader.nextBoolean();

            case NULL:
                return null;

            default:
                throw new IllegalStateException(next.name());
        }
    }

}
