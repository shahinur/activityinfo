package org.activityinfo.model.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class IsRecordDeserializer<T extends IsRecord> extends JsonDeserializer<T> {

    private Class<T> type;
    private final Method deserializationMethod;

    public IsRecordDeserializer(Class<T> type) {
        this.type = type;
        this.deserializationMethod = findDeserializationMethod(type);
    }

    private static Method findDeserializationMethod(Class<? extends IsRecord> type) {
        Method method;
        try {
            method = type.getMethod("fromRecord", Record.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(type.getName() + " does not have a fromRecord(Record record) method needed" +
                                       " for deserialization");
        }
        if(!Modifier.isPublic(method.getModifiers())) {
            throw new RuntimeException(method.getName() + " must be public.");
        }
        if(!Modifier.isStatic(method.getModifiers())) {
            throw new RuntimeException(method.getName() + " must be static.");
        }
        if(!method.getReturnType().equals(type)) {
            throw new RuntimeException(method.getName() + " must return " + type.getName());
        }
        return method;
    }

    @Override
    public T deserialize(JsonParser jsonParser,
                                DeserializationContext deserializationContext) throws IOException {

        Record record = RecordSerialization.readRecord(jsonParser);
        try {
            return type.cast(deserializationMethod.invoke(null, record));
        } catch (IllegalAccessException e) {
            throw new JsonParseException("Exception invoking fromRecord() on " + type.getName() + " during " +
                                       "deserialization.", jsonParser.getCurrentLocation(), e);
        } catch (InvocationTargetException e) {
            throw new JsonParseException("Exception in fromRecord() during deserialization",
                    jsonParser.getCurrentLocation(),
                    e.getCause());
        }
    }
}
