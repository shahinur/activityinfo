package org.activityinfo.store.hrd.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.json.RecordSerialization;
import org.activityinfo.model.resource.PropertyBag;
import org.activityinfo.model.resource.Resource;

import java.io.IOException;
import java.io.StringWriter;

public class Content {


    public static final String CONTENTS_PROPERTY = "c";

    private static String writePropertiesAsString(PropertyBag bag) {
        try {
            StringWriter writer = new StringWriter();
            JsonGenerator json = ObjectMapperFactory.get().getFactory().createGenerator(writer);
            json.writeStartObject();
            RecordSerialization.writeProperties(json, bag);
            json.writeEndObject();
            json.close();
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("Exception serializing property bag: " + e.getMessage(), e);
        }
    }

    public static void readPropertiesFromString(PropertyBag bag, Text text) {
        try {
            JsonParser json = ObjectMapperFactory.get().getFactory().createParser(text.getValue());
            json.nextToken();
            assert json.getCurrentToken() == JsonToken.START_OBJECT;
            RecordSerialization.readProperties(json, bag);
        } catch(IOException e) {
            throw new RuntimeException("Exception deserializing property bag: " + e.getMessage(), e);
        }
    }

    public static void readProperties(Entity entity, Resource resource) {
        readPropertiesFromString(resource, (Text) entity.getProperty(CONTENTS_PROPERTY));
    }

    public static void writeProperties(Resource resource, Entity entity) {
        entity.setProperty(CONTENTS_PROPERTY, new Text(writePropertiesAsString(resource)));
    }
}
