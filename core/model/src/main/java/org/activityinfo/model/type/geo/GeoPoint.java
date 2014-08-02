package org.activityinfo.model.type.geo;

import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;

/**
 * A Field Value containing a geographic point in the WGS84 geographic
 * reference system.
 */
public class GeoPoint implements FieldValue, IsRecord {

    private double latitude;
    private double longitude;

    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return GeoPointType.TYPE_CLASS;
    }

    @Override
    public Record toRecord() {
        return new Record()
            .set(TYPE_CLASS_FIELD_NAME, getTypeClass().getId())
            .set("latitude", latitude)
            .set("longitude", longitude);
    }

    public static GeoPoint fromRecord(Record record) {
        return new GeoPoint(record.getDouble("latitude"), record.getDouble("longitude"));
    }
}
