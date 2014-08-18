package org.activityinfo.ui.store.remote.client.serde;

import com.github.nmorel.gwtjackson.client.AbstractConfiguration;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

public class JacksonConfiguration extends AbstractConfiguration {
    @Override
    protected void configure() {
        type(ResourceId.class)
            .serializer(ResourceIdJsonSerializer.class)
            .deserializer(ResourceIdJsonDeserializer.class);

        type(Resource.class)
            .serializer(ResourceSerializer.class)
            .deserializer(ResourceDeserializer.class);
    }
}
