package org.activityinfo.ui.store.remote.client;

import com.github.nmorel.gwtjackson.client.JsonSerializationContext;
import com.github.nmorel.gwtjackson.client.JsonSerializer;
import com.github.nmorel.gwtjackson.client.JsonSerializerParameters;
import com.github.nmorel.gwtjackson.client.stream.JsonWriter;
import org.activityinfo.model.resource.PropertyBag;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;

import javax.annotation.Nonnull;
import java.util.Map;

public class ResourceSerializer extends JsonSerializer<Resource> {
    @Override
    protected void doSerialize(JsonWriter writer,
                               @Nonnull Resource resource,
                               JsonSerializationContext ctx,
                               JsonSerializerParameters params) {

        writer.beginObject();
        writer.name("@id");
        writer.value(resource.getId().asString());
        writer.name("@owner");
        writer.name(resource.getOwnerId().asString());
        writeProperties(writer, resource);
        writer.endObject();
    }

    private void writeRecord(JsonWriter writer, Record value) {
        writer.beginObject();
        writeProperties(writer, value);
        writer.endObject();
    }

    private void writeProperties(JsonWriter writer, PropertyBag<?> resource) {
        for (Map.Entry<String, Object> entry : resource.getProperties().entrySet()) {
            writer.name(entry.getKey());
            Object value = entry.getValue();
            if(value instanceof String) {
                writer.value((String) value);
            } else if(value instanceof Number) {
                writer.value((Number)value);
            } else if(value instanceof Boolean) {
                writer.value(value == Boolean.TRUE);
            } else if(value instanceof Record) {
                writeRecord(writer, (Record)value);
            }
        }
    }
}
