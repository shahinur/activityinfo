package org.activityinfo.model.type.geo;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;

public enum GeoPointType implements FieldType, FieldTypeClass {

    INSTANCE;

    public static final String TYPE_ID = "GEOGRAPHIC_POINT";


    @Override
    public String getId() {
        return TYPE_ID;
    }

    @Override
    public String getLabel() {
        return "Latitude/Longitude";
    }

    @Override
    public FieldType createType(Record typeParameters) {
        return this;
    }

    @Override
    public FieldType createType() {
        return this;
    }


    @Override
    public FormClass getParameterFormClass() {
        return new FormClass(ResourceId.create("_geoPoint"));
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
    public ComponentReader<String> getStringReader(String fieldName, String componentId) {
        return new NullComponentReader<>();
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(String name, String componentId) {
        return new NullComponentReader<>();
    }

    @Override
    public String getXFormType() {
        return null;
    }

    public static Record fromXY(double x, double y) {
        return new Record()
                .set(TYPE_FIELD_NAME, INSTANCE.getId())
                .set("x", x)
                .set("y", y);
    }

    public static AiLatLng asLatLng(Record record) {
        return new AiLatLng(record.getDouble("x"), record.getDouble("y"));
    }

    public static Record fromLatLng(AiLatLng value) {
        return fromXY(value.getLng(), value.getLat());
    }


}
