package org.activityinfo.ui.store.remote.client;

import com.github.nmorel.gwtjackson.client.JsonDeserializationContext;
import com.github.nmorel.gwtjackson.client.JsonDeserializer;
import com.github.nmorel.gwtjackson.client.JsonDeserializerParameters;
import com.github.nmorel.gwtjackson.client.stream.JsonReader;
import org.activityinfo.model.resource.ResourceId;

public class ResourceIdJsonDeserializer extends JsonDeserializer<ResourceId> {
    @Override
    protected ResourceId doDeserialize(JsonReader reader,
                                                       JsonDeserializationContext ctx,
                                                       JsonDeserializerParameters params) {
        return ResourceId.valueOf(reader.nextString());
    }
}
