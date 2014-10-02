package org.activityinfo.store.tasks.export;

import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumValue;

public class BinaryEnumConverter implements FieldValueConverter<EnumFieldValue> {

    private EnumValue enumValue;

    public BinaryEnumConverter(EnumValue enumValue) {
        this.enumValue = enumValue;
    }

    @Override
    public Object convertValue(EnumFieldValue fieldValue) {
        if(fieldValue.getResourceIds().contains(enumValue.getId())) {
            return true;
        } else {
            return false;
        }
    }
}
