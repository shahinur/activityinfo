package org.activityinfo.model.type.time;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.component.ComponentReader;

import java.util.Date;

public enum LocalDateType implements FieldType, FieldTypeClass {

    INSTANCE;

    public static final String TYPE_ID = "LOCAL_DATE";

    @Override
    public String getId() {
        return TYPE_ID;
    }

    @Override
    public String getLabel() {
        return "Date";
    }

    @Override
    public FieldType createType(Record typeParameters) {
        // no parameters
        return this;
    }

    @Override
    public FieldType createType() {
        return this;
    }

    @Override
    public FormClass getParameterFormClass() {
        return new FormClass(ResourceId.create("_type:localDate"));
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return this;
    }



    @Override
    public Record getParameters() {
        return new Record();
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


    public static LocalDate asLocalDate(Record record) {
        return LocalDate.parse(record.getString("date"));
    }

    public static Record fromDate(Date date) {
        return fromLocalDate(new LocalDate(date));
    }

    public static Record fromLocalDate(LocalDate value) {
        Record record = new Record();
        record.set(TYPE_FIELD_NAME, INSTANCE.getId());
        record.set("date", value.toString());
        return record;
    }


}
