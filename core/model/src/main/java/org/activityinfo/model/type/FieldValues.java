package org.activityinfo.model.type;

import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.primitive.TextValue;

public class FieldValues {

    /**
     * Deserializes the {@code FieldValue} named {@code fieldName} from {@code resource} if the
     * field value matches the given {@code typeClass}, otherwise {@code null}
     *
     */
    public static <T extends FieldValue> T readFieldValueIfType(Resource resource,
                                                                String fieldName,
                                                                RecordFieldTypeClass typeClass) {

        Record record = resource.isRecord(fieldName);
        if(record != null) {
            if (record.getString(FieldValue.TYPE_CLASS_FIELD_NAME).equals(typeClass.getId())) {
                return (T)typeClass.deserialize(record);
            }
        }
        return null;
    }
}
