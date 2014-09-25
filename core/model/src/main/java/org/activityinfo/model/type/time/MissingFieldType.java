package org.activityinfo.model.type.time;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Records;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.MissingTypeClass;

public class MissingFieldType implements FieldType {

    public static final MissingFieldType INSTANCE = new MissingFieldType();

    private MissingFieldType() {

    }

    @Override
    public FieldTypeClass getTypeClass() {
        return MissingTypeClass.INSTANCE;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitMissingField(field, this);
    }

    @Override
    public Record asRecord() {
        return Records.builder()
            .set(TYPE_CLASS_FIELD_NAME, getTypeClass().getId())
            .build();

    }
}
