package org.activityinfo.model.type;

import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;

public class SubFormValue implements FieldValue, IsRecord {

    private ResourceId classId;
    private Record fields;

    public SubFormValue(ResourceId classId, Record fields) {

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
        record.set("class", new ReferenceValue(classId));
        record.set("fields", fields);
        return record;
    }
}
