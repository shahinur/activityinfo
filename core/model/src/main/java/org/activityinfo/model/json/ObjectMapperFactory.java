package org.activityinfo.model.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.activityinfo.model.resource.Resource;

public class ObjectMapperFactory {

    public static ObjectMapper get() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Resource.class, new ResourceSerializer());
        module.addDeserializer(Resource.class, new ResourceDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

}
