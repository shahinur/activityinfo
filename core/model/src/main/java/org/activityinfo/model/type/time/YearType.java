package org.activityinfo.model.type.time;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.RecordFieldTypeClass;
import org.activityinfo.model.type.SingletonTypeClass;

/**
 * Value type that represents a calendar year in the ISO-8601 calendar.
 * There is no representation of time-of-day or time-zone.
 */
public class YearType implements FieldType, TemporalType {

    public interface TypeClass extends SingletonTypeClass, RecordFieldTypeClass {}

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
}
