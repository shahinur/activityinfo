package org.activityinfo.model.type.time;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.RecordFieldTypeClass;
import org.activityinfo.model.type.SingletonTypeClass;

/**
 * Value type that represents a continuous interval between two {@link org.activityinfo.model.type.time.LocalDate}s,
 * starting on {@code startDate}, inclusive, and ending on {@code endDate}, inclusive.
 */
public class LocalDateIntervalType implements FieldType {

    public interface TypeClass extends RecordFieldTypeClass, SingletonTypeClass {}

    public static final TypeClass TYPE_CLASS = new TypeClass() {
        @Override
        public String getId() {
            return "localDateInterval";
        }

        @Override
        public FieldType createType() {
            return LocalDateIntervalType.INSTANCE;
        }

        @Override
        public LocalDateInterval deserialize(Record record) {
            return LocalDateInterval.fromRecord(record);
        }
    };

    public static final LocalDateIntervalType INSTANCE = new LocalDateIntervalType();

    private LocalDateIntervalType() {
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }
}
