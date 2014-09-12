package org.activityinfo.model.type.geo;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.RecordFieldTypeClass;
import org.activityinfo.model.type.TypeFieldType;

/**
 * A value type describing a point within the WGS84 Geographic Reference System.
 */
public class GeoPointType implements FieldType {

    public static final String TYPE_ID = "GEOGRAPHIC_POINT";

    public static final GeoPointType INSTANCE = new GeoPointType();

    public interface TypeClass extends RecordFieldTypeClass<GeoPoint> { }

    public static final TypeClass TYPE_CLASS = new TypeClass() {
        @Override
        public String getId() {
            return TYPE_ID;
        }

        @Override
        public String getLabel() {
            return "Latitude/Longitude";
        }

        @Override
        public FieldType createType() {
            return INSTANCE;
        }

        @Override
        public GeoPoint deserialize(Record record) {
            return GeoPoint.fromRecord(record);
        }
    };

    private GeoPointType() {  }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitGeoPointField(field, this);
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }

}
