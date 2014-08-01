package org.activityinfo.model.type.time;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.component.ComponentReader;

/**
 * Value type that represents a continuous interval between two {@link org.activityinfo.model.type.time.LocalDate}s,
 * starting on {@code startDate}, inclusive, and ending on {@code endDate}, inclusive.
 */
public class LocalDateIntervalType implements FieldType {

    public static final FieldTypeClass TYPE_CLASS = new FieldTypeClass() {
        @Override
        public String getId() {
            return "localDateInterval";
        }

        @Override
        public String getLabel() {
            return "Date Range";
        }

        @Override
        public FieldType createType() {
            return LocalDateIntervalType.INSTANCE;
        }
    };

    public static final LocalDateIntervalType INSTANCE = new LocalDateIntervalType();

    private LocalDateIntervalType() {
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }


    @Override
    public ComponentReader<String> getStringReader(String fieldName, String componentId) {
        return null;
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(String fieldName, String componentId) {
        return null;
    }

}
