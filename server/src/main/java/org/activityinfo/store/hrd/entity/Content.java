package org.activityinfo.store.hrd.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.json.RecordSerialization;
import org.activityinfo.model.resource.*;

import java.io.IOException;
import java.io.StringWriter;

public class Content {

    public static final String OWNER_PROPERTY = "O";

    public static final String VERSION_PROPERTY = "V";

    public static final String LABEL_PROPERTY = "L";

    public static final String CLASS_PROPERTY = "C";

    public static final String CONTENTS_PROPERTY = "P";

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

    public static Resource deserializeResource(Entity entity) {
        Resource resource = Resources.createResource();
        resource.setId(ResourceId.valueOf(entity.getKey().getParent().getName()));
        resource.setVersion((Long)entity.getProperty(VERSION_PROPERTY));
        resource.setOwnerId(ResourceId.valueOf((String) entity.getProperty(OWNER_PROPERTY)));
        readProperties(entity, resource);
        return resource;
    }

    public static ResourceNode deserializeResourceNode(Entity entity) {
        ResourceId id = ResourceId.valueOf(entity.getKey().getName());
        ResourceNode resource = new ResourceNode(id);
        resource.setId(id);
        resource.setVersion((Long) entity.getProperty(VERSION_PROPERTY));
        resource.setLabel((String)entity.getProperty(LABEL_PROPERTY));
        resource.setOwnerId(ResourceId.valueOf((String) entity.getProperty(OWNER_PROPERTY)));
        if(entity.getProperty(CLASS_PROPERTY) instanceof String) {
            resource.setClassId(ResourceId.valueOf((String) entity.getProperty(CLASS_PROPERTY)));
        }
        return resource;
    }
}
