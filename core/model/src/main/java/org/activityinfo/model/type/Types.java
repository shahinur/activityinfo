package org.activityinfo.model.type;

import org.activityinfo.model.resource.PropertyBag;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;

public class Types {

    public static <V extends FieldValue> V read(PropertyBag bag, String name, RecordFieldTypeClass<V> typeClass) {
        Record record = bag.isRecord(name);
        if(record != null) {
            String typeName = record.getString(FieldValue.TYPE_CLASS_FIELD_NAME);
            if(typeClass.getId().equals(typeName)) {
                return typeClass.deserialize(record);
            }
        }
        return null;
    }

    public static ResourceId readReference(PropertyBag bag, String name) {
        ReferenceValue value = read(bag, name, ReferenceType.TYPE_CLASS);
        return value.getResourceId();
    }

    public static ResourceId parameterFormClassId(FieldTypeClass typeClass) {
        return ResourceId.valueOf("_type:" + typeClass.getId());
    }
}
