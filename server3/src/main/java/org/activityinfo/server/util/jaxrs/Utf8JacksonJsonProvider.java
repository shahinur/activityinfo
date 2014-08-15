package org.activityinfo.server.util.jaxrs;

import com.bedatadriven.geojson.jackson2.GeoJsonModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Inject;

import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


/**
 * Wraps the JacksonJsonProvider to refine the ObjectMapper and
 * to ensure that the Content-Type header always includes the
 * charset=UTF-8 fragment
 */
@Singleton
public class Utf8JacksonJsonProvider extends JacksonJsonProvider {

    @Inject
    public Utf8JacksonJsonProvider(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void writeTo(Object value,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException {

        httpHeaders.putSingle(HttpHeaders.CONTENT_TYPE, mediaType.toString() + ";charset=UTF-8");

        super.writeTo(value, type, genericType, annotations, mediaType, httpHeaders, entityStream);

    }

}
