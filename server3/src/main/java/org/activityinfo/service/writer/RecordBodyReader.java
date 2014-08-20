package org.activityinfo.service.writer;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resources;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

public class RecordBodyReader implements MessageBodyReader<IsRecord> {

    private static final Logger LOGGER = Logger.getLogger(RecordBodyReader.class.getName());

    private JsonParser jsonParser = new JsonParser();

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return IsRecord.class.isAssignableFrom(type) &&
               mediaType.equals(MediaType.APPLICATION_JSON_TYPE);
    }

    @Override
    public IsRecord readFrom(Class<IsRecord> type,
                             Type genericType,
                             Annotation[] annotations,
                             MediaType mediaType,
                             MultivaluedMap<String, String> httpHeaders,
                             InputStream entityStream) throws IOException, WebApplicationException {

        JsonObject jsonObject = parseJsonObject(entityStream);
        Record record = parseRecord(jsonObject);

        Method fromRecord;
        try {
            fromRecord = type.getMethod("fromRecord", Record.class);
        } catch (NoSuchMethodException e) {
            LOGGER.severe("Expected class " + type.getName() + " to have a static fromRecord(Record) method.");
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        if(!Modifier.isStatic(fromRecord.getModifiers()) ||
           !Modifier.isPublic(fromRecord.getModifiers())) {
            LOGGER.severe("Expected method " + fromRecord + " to be public static");
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        try {
            return (IsRecord)fromRecord.invoke(null, record);
        } catch (InvocationTargetException e) {
            throw new WebApplicationException(Response
                .status(Response.Status.BAD_REQUEST)
                .entity("Exception deserializing record: " + e.getCause().getMessage())
                .build());
        } catch (IllegalAccessException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Record parseRecord(JsonObject jsonObject) {
        Record record;
        try {
            record = Resources.recordFromJson(jsonObject);
        } catch (Exception e) {
            LOGGER.log(SEVERE, "Exception parsing Record", e);
            throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Malformed JSON in request: " + e.getMessage())
                    .build());
        }
        return record;
    }

    private JsonObject parseJsonObject(InputStream entityStream) {
        JsonElement element;
        try {
            element = jsonParser.parse(new InputStreamReader(entityStream, Charsets.UTF_8));
        } catch(JsonParseException e) {
            LOGGER.log(SEVERE, "Exception parsing JSON", e);
            throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Malformed JSON in request: " + e.getMessage())
                    .build());
        }

        if(!element.isJsonObject()) {
            throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Expected JSON object")
                    .build());
        }
        return element.getAsJsonObject();
    }
}
