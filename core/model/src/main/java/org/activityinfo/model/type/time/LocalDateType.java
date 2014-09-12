package org.activityinfo.model.type.time;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.*;

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
public class LocalDateType implements FieldType, TemporalType {

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
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitLocalDateField(field, this);
    }

    @Override
    public String toString() {
        return "LocalDateType";
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }
}
