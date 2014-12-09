package org.activityinfo.model.type.time;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.RecordFieldTypeClass;
import org.activityinfo.model.type.SingletonTypeClass;

/**
 * Value type that represents a calendar month in the ISO-8601 calendar.
 * There is no representation of time-of-day or time-zone.
 */
public class MonthType implements FieldType, TemporalType {

    public interface TypeClass extends SingletonTypeClass, RecordFieldTypeClass {}

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
}
