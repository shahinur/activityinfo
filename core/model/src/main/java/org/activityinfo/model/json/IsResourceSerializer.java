package org.activityinfo.model.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.activityinfo.model.resource.IsResource;

import java.io.IOException;

public class IsResourceSerializer extends JsonSerializer<IsResource> {

    private ResourceSerializer resourceSerializer = new ResourceSerializer();

    @Override
    public void serialize(IsResource value,
                          JsonGenerator jgen,
                          SerializerProvider provider) throws IOException, JsonProcessingException {

        jgen.writeObject(value.asResource());
       // resourceSerializer.serialize(value.asResource(), jgen, provider);
    }


}
