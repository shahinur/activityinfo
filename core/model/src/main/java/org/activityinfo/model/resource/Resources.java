package org.activityinfo.model.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.activityinfo.model.record.IsRecord;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Resources {

    /**
     * The resource id of the user database.
     *
     * All registered users are resources owned by this
     * resource.
     */
    public static final ResourceId USER_DATABASE_ID = ResourceId.valueOf("_users");

    public static final int RADIX = 10;

    public static final ResourceId ROOT_ID = ResourceId.valueOf("_root");

    public static long COUNTER = 1;

    public static Resource createResource(Record record) {
        Resource resource = new Resource();
        resource.setId(generateId());
        resource.setValue(record);
        return resource;
    }

    public static Resource createResource(RecordBuilder recordBuilder) {
        return createResource(recordBuilder.build());
    }

    public static ResourceId generateId() {
        return ResourceId.valueOf("c" + Long.toString(new Date().getTime(), Character.MAX_RADIX) +
                                  Long.toString(COUNTER++, Character.MAX_RADIX));
    }

    /**
     * Creates a new resource with the given
     * @param parentId
     * @param value
     * @return
     */
    public static Resource newResource(ResourceId parentId, IsRecord value) {
        Resource resource = new Resource();
        resource.setId(generateId());
        resource.setOwnerId(parentId);
        resource.setValue(value.asRecord());
        resource.setVersion(0L);
        return resource;
    }

    public static Resource fromJson(String json) {
        JsonParser parser = new JsonParser();
        JsonObject resourceObject = parser.parse(json).getAsJsonObject();

        return fromJson(resourceObject);
    }

    public static Resource fromJson(JsonObject resourceObject) {
        RecordBuilder recordBuilder = Records.builder();
        ResourceId id = null;
        ResourceId ownerId = null;

        for (Map.Entry<String, JsonElement> property : resourceObject.entrySet()) {
            String name = property.getKey();
            switch (name) {
                case "@id":
                    id = ResourceId.valueOf(resourceObject.getAsJsonPrimitive(name).getAsString());
                    break;
                case "@owner":
                    ownerId = ResourceId.valueOf(resourceObject.getAsJsonPrimitive(name).getAsString());
                    break;
                default:
                    // normal value
                    if (!property.getValue().isJsonNull()) {
                        set(recordBuilder, property.getKey(), propertyFromJson(property.getValue()));
                    }
            }
        }

        return Resources.createResource(recordBuilder).setId(id).setOwnerId(ownerId);
    }

    private static Object propertyFromJson(JsonElement propertyValue) {
        assert !propertyValue.isJsonNull();

        if(propertyValue.isJsonPrimitive()) {
            JsonPrimitive value = propertyValue.getAsJsonPrimitive();
            if(value.isString()) {
                return value.getAsString();
            } else if(value.isNumber()) {
                return value.getAsDouble();
            } else if(value.isBoolean()) {
                return value.getAsBoolean();
            } else {
                throw new UnsupportedOperationException("value: " + value);
            }
        } else if(propertyValue.isJsonArray()) {
            return arrayFromJson(propertyValue);

        } else if(propertyValue.isJsonObject()) {
            return recordFromJson(propertyValue.getAsJsonObject());
        } else {
            throw new UnsupportedOperationException("value: " + propertyValue);
        }
    }

    private static Object arrayFromJson(JsonElement propertyValue) {
        List list = new ArrayList();
        JsonArray array = propertyValue.getAsJsonArray();
        for(int i=0;i!=array.size();++i) {
            list.add(propertyFromJson(array.get(i)));
        }
        return list;
    }

    private static Record recordFromJson(JsonObject jsonObject) {
        RecordBuilder record = Records.builder();

        for(Map.Entry<String, JsonElement> field : jsonObject.entrySet()) {
            if(!field.getValue().isJsonNull()) {
                Object property = propertyFromJson(field.getValue());
                set(record, field.getKey(), property);
            }
        }

        return record.build();
    }

    private static void set(RecordBuilder recordBuilder, String key, Object property) {
        if (property instanceof String) {
            recordBuilder.set(key, (String) property);
        } else if (property instanceof Double) {
            recordBuilder.set(key, (Double) property);
        } else if (property instanceof Boolean) {
            recordBuilder.set(key, (Boolean) property);
        } else if (property instanceof Record) {
            recordBuilder.set(key, (Record) property);
        } else if (property instanceof List) {
            recordBuilder.set(key, (List) property);
        } else {
            throw new UnsupportedOperationException("value: " + property);
        }
    }

    public static String toJson(Resource resource) {
        JsonObject resourceObject = toJsonObject(resource);

        return resourceObject.toString();
    }

    public static JsonObject toJsonObject(Resource resource) {
        JsonObject resourceObject = new JsonObject();
        resourceObject.addProperty("@id", resource.getId().asString());
        resourceObject.addProperty("@owner", resource.getOwnerId().asString());

        for(Map.Entry<String, Object> property : resource.getValue().asMap().entrySet()) {
            if(property.getValue() != null) {
                resourceObject.add(property.getKey(), propertyValueToJson(property.getValue()));
            }
        }
        return resourceObject;
    }

    private static JsonElement propertyValueToJson(Object value) {
        if(value instanceof String) {
            return new JsonPrimitive((String)value);
        } else if(value instanceof Number) {
            return new JsonPrimitive((Number)value);
        } else if(value instanceof Boolean) {
            return new JsonPrimitive((Boolean)value);
        } else if(value instanceof List) {
            return toJsonArray((List)value);
        } else if(value instanceof Record) {
            return toJsonObject((Record)value);
        } else {
            throw new UnsupportedOperationException("value: " + value + " (" + value.getClass().getName() + ")") ;
        }
    }

    public static JsonElement toJsonObject(Record value) {
        JsonObject jsonObject = new JsonObject();
        for(Map.Entry<String, Object> property :  value.asMap().entrySet()) {
            if(property.getValue() != null) {
                jsonObject.add(property.getKey(), propertyValueToJson(property.getValue()));
            }
        }
        return jsonObject;
    }

    private static JsonElement toJsonArray(List array) {
        JsonArray jsonArray = new JsonArray();
        for(Object element : array) {
            jsonArray.add(propertyValueToJson(element));
        }
        return jsonArray;
    }
}
