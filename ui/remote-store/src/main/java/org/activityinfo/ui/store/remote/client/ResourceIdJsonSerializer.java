package org.activityinfo.ui.store.remote.client;

import com.github.nmorel.gwtjackson.client.JsonSerializationContext;
import com.github.nmorel.gwtjackson.client.JsonSerializer;
import com.github.nmorel.gwtjackson.client.JsonSerializerParameters;
import com.github.nmorel.gwtjackson.client.stream.JsonWriter;
import org.activityinfo.model.resource.ResourceId;

import javax.annotation.Nonnull;

public class ResourceIdJsonSerializer extends JsonSerializer<ResourceId> {
    @Override
    protected void doSerialize(JsonWriter writer,
                               @Nonnull ResourceId value,
                               JsonSerializationContext ctx,
                               JsonSerializerParameters params) {
        writer.value(value.asString());
    }

}
