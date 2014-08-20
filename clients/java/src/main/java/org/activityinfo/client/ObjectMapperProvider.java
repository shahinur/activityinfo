package org.activityinfo.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activityinfo.model.json.ObjectMapperFactory;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper = ObjectMapperFactory.get();

    @Override
    public ObjectMapper getContext(Class type) {
        return objectMapper;
    }
}
