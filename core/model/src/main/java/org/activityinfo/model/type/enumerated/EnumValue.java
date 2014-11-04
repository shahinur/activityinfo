package org.activityinfo.model.type.enumerated;

import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;

public class EnumValue implements FieldValue, IsRecord {
    private ResourceId id;
    private String label;

    public EnumValue(ResourceId id, String label) {
        this.id = id;
        this.label = label;
    }

    public ResourceId getId() {
        return id;
    }

    public void setId(ResourceId id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static EnumValue fromRecord(Record record) {
        return new EnumValue(ResourceId.create(record.getString("id")), record.getString("label"));
    }

    @Override
    public String toString() {
        return id + ":" + label;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return EnumType.TYPE_CLASS;
    }

    @Override
    public Record asRecord() {
        Record record = new Record();
        record.set(TYPE_CLASS_FIELD_NAME, EnumType.TYPE_CLASS.getId()).
                set("label", label).
                set("id", id.asString());
        return record;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnumValue enumValue = (EnumValue) o;

        if (id != null ? !id.equals(enumValue.id) : enumValue.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
