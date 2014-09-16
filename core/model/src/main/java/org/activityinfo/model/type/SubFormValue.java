package org.activityinfo.model.type;

import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;

import javax.annotation.Nonnull;

public class SubFormValue implements FieldValue, IsRecord {

    @Nonnull
    private ResourceId classId;

    @Nonnull
    private Record fields;

    public SubFormValue(@Nonnull ResourceId classId, @Nonnull Record fields) {
        this.classId = classId;
        this.fields = fields;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return SubFormType.TYPE_CLASS;
    }

    public ResourceId getClassId() {
        return classId;
    }

    public Record getFields() {
        return fields;
    }

    public <V extends FieldValue> V get(String name, RecordFieldTypeClass<V> typeClass) {
        return Types.read(fields, name, typeClass);
    }

    public String getString(String name) {
        return fields.isString(name);
    }

    @Override
    public Record asRecord() {
        Record record = new Record();
        record.set(TYPE_CLASS_FIELD_NAME, getTypeClass().getId());
        record.set("class", new ReferenceValue(classId).asRecord());
        record.set("fields", fields);
        return record;
    }

}
