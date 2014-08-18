package org.activityinfo.service.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.service.store.UpdateResult;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Produces("*/*")
public class UpdateResultWriter implements MessageBodyWriter<UpdateResult> {

    private final ObjectMapper objectMapper = ObjectMapperFactory.get();

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(UpdateResult.class);
    }

    @Override
    public long getSize(UpdateResult updateResult,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(UpdateResult updateResult,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {

        // In addition to writing the JSON body, translate the headers to HTTP language
        // in terms of status codes and ETags

        switch(updateResult.getStatus()) {
            case COMMITTED:
                throw new WebApplicationException(Response
                        .status(Response.Status.CREATED)
                        .tag(EntityTags.ofResource(updateResult.getId(), updateResult.getNewVersion()))
                        .entity(objectMapper.writeValueAsString(updateResult))
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build());

            case PENDING:
                throw new WebApplicationException(Response
                        .status(Response.Status.ACCEPTED)
                        .build());

            case REJECTED:
                throw new WebApplicationException(Response
                        .status(Response.Status.CONFLICT)
                        .build());
        }
    }
}
