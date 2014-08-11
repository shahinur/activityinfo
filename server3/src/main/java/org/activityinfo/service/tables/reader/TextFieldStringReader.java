package org.activityinfo.service.tables.reader;

import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.TypeRegistry;
import org.activityinfo.model.type.primitive.HasStringValue;

public class TextFieldStringReader implements StringFieldReader {
    private final String fieldName;

    public TextFieldStringReader(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String readString(Resource resource) {
        Object value = resource.get(fieldName);
        if(value instanceof String) {
            return (String)value;
        } else if(value instanceof Record) {
            FieldValue fieldValue = TypeRegistry.get().deserializeFieldValue((Record)value);
            if(fieldValue instanceof HasStringValue) {
                return ((HasStringValue) fieldValue).asString();
            }
        }
        return null;
    }
}
