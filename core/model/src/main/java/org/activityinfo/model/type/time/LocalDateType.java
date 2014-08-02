package org.activityinfo.model.type.time;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.RecordFieldTypeClass;
import org.activityinfo.model.type.component.ComponentReader;

/**
 * Value type that represents a date in the ISO-8601 calendar.
 * There is no representation of time-of-day or time-zone.
 *
 * <blockquote>
 * The “local” terminology is familiar from Joda-Time and comes originally from the ISO-8601 date and time standard.
 * It relates specifically to the absence of a time-zone. In effect, a local date is a description of a date,
 * such as the “5th April 2014”. That particular local date will start at different points on the time-line
 * depending on where on the Earth you are. Thus the local date will start in Australia 10 hours before it
 * starts in London and 18 hours before it starts in San Francisco. -- Stephen Colebourn in
 * <a href="http://www.infoq.com/articles/java.time">InfoQ</a>
 * </blockquote>
 *
 */
public class LocalDateType implements FieldType {

    public static final String TYPE_ID = "LOCAL_DATE";

    public static final FieldTypeClass TYPE_CLASS = new RecordFieldTypeClass() {
        @Override
        public String getId() {
            return TYPE_ID;
        }

        @Override
        public String getLabel() {
            return "Date";
        }

        @Override
        public FieldType createType() {
            return INSTANCE;
        }

        @Override
        public FieldValue deserialize(Record record) {
            return org.activityinfo.model.type.time.LocalDate.fromRecord(record);
        }
    };

    public static final LocalDateType INSTANCE = new LocalDateType();


    private LocalDateType() { }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public ComponentReader<String> getStringReader(final String fieldName, String componentId) {
        return new ComponentReader<String>() {
            @Override
            public String read(Resource resource) {
                Record record = resource.isRecord(fieldName);
                if(record == null) {
                    return null;
                } else {
                    return record.getString("date");
                }
            }
        };
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(final String name, String componentId) {
        return new ComponentReader<LocalDate>() {
            @Override
            public LocalDate read(Resource resource) {
                Record record = resource.isRecord(name);
                if(record == null) {
                    return null;
                }
                return LocalDate.parse(record.getString("date"));
            }
        };
    }

    @Override
    public String getXFormType() {
        return "date";
    }
}
