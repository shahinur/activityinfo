package org.activityinfo.model.type.time;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.RecordFieldTypeClass;
import org.activityinfo.model.type.TypeFieldType;

/**
 * Value type that represents a calendar month in the ISO-8601 calendar.
 * There is no representation of time-of-day or time-zone.
 */
public class MonthType implements FieldType, TemporalType {

    public interface TypeClass extends RecordFieldTypeClass<MonthValue> {}

    public static final TypeClass TYPE_CLASS = new TypeClass() {
        @Override
        public MonthValue deserialize(Record record) {
            return new MonthValue(record.getInt("year"), record.getInt("month"));
        }

        @Override
        public String getId() {
            return "month";
        }

        @Override
        public String getLabel() {
            return "Month";
        }

        @Override
        public FieldType createType() {
            return INSTANCE;
        }
    };

    public static final MonthType INSTANCE = new MonthType();

    private MonthType() {}

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitMonthField(field, this);
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }
}
