package org.activityinfo.server.endpoint.rest;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class Jackson {

    static JsonGenerator createJsonFactory(StringWriter writer) throws IOException {
        JsonFactory jfactory = new JsonFactory();
        JsonGenerator json = jfactory.createGenerator(writer);
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        json.setPrettyPrinter(prettyPrinter);
        return json;
    }

    /**
     * Creates json mapper.
     *
     * @return json mapper
     */
    public static ObjectMapper createJsonMapper() {
        final AnnotationIntrospector jaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        final AnnotationIntrospector jackson = new JacksonAnnotationIntrospector();

        final AnnotationIntrospector pair = AnnotationIntrospector.pair(jackson, jaxb);

        final ObjectMapper mapper = new ObjectMapper();
        mapper.setAnnotationIntrospectors(jackson, jaxb);
        return mapper;
    }

    public static void writeMap(JsonGenerator json, String fieldName, Map<String, Object> mapValue) throws IOException {
        json.writeObjectFieldStart(fieldName);
        for (Map.Entry<String, Object> entry : mapValue.entrySet()) {
            final Object value = entry.getValue();
            if (value instanceof Boolean) {
                json.writeBooleanField(entry.getKey(), (Boolean) value);
            } else if (value instanceof Double) {
                json.writeNumberField(entry.getKey(), (Double) value);
            }
        }
        json.writeEndObject();
    }

}
