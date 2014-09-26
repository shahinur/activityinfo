package org.activityinfo.model.type.time;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.*;

/**
 * Value type that represents a calendar year in the ISO-8601 calendar.
 * There is no representation of time-of-day or time-zone.
 */
public class YearType implements FieldType, TemporalType {

    public interface TypeClass extends SingletonTypeClass, RecordFieldTypeClass<YearValue> {}

    public static final TypeClass TYPE_CLASS = new TypeClass() {
        @Override
        public YearValue deserialize(Record record) {
            return new YearValue(record.getInt("year"));
        }

        @Override
        public String getId() {
            return "year";
        }

        @Override
        public String getLabel() {
            return "Year";
        }

        @Override
        public FieldType createType() {
            return INSTANCE;
        }
    };

    public static final YearType INSTANCE = new YearType();

    private YearType() {}

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitYearField(field, this);
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }

}
