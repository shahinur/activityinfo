package org.activityinfo.model.type.time;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.RecordFieldTypeClass;
import org.activityinfo.model.type.TypeFieldType;

public class InstantType implements FieldType {

    public interface TypeClass extends RecordFieldTypeClass<Instant> {}

    public static final InstantType INSTANCE = new InstantType();

    public static final RecordFieldTypeClass TYPE_CLASS = new TypeClass() {
        @Override
        public Instant deserialize(Record record) {
            return Instant.fromRecord(record);
        }

        @Override
        public String getId() {
            return "instant";
        }

        @Override
        public FieldType createType() {
            return INSTANCE;
        }
    };

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }
}
