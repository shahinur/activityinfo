package org.activityinfo.model.type.geo;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.activityinfo.model.record.IsRecord;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;

import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

/**
 * A Field Value containing a geographic point in the WGS84 geographic
 * reference system.
 */
public class GeoPoint implements FieldValue, Serializable, IsRecord {

    private double latitude;
    private double longitude;

    public GeoPoint() {
        latitude = 0;
        longitude = 0;
    }

    public GeoPoint(double latitude, double longitude) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @return The latitude of the point (y-axis)
     */
    @XmlAttribute(name = "y")
    public double getLatitude() {
        return latitude;
    }

    /**
     * Required for XML serializaiton
     *
     * @param latitude
     */
    void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * REquired for XML serialization
     *
     * @param longitude
     */
    void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return The longitude of the point (x-axis)
     */
    @XmlAttribute(name = "x")
    public double getLongitude() {
        return longitude;
    }


    @Override
    public FieldTypeClass getTypeClass() {
        return GeoPointType.TYPE_CLASS;
    }

    @Override
    public Record asRecord() {
        return Records.builder()
                .set(TYPE_CLASS_FIELD_NAME, getTypeClass().getId())
                .set("latitude", latitude)
                .set("longitude", longitude)
                .build();
    }

    public static GeoPoint fromRecord(Record record) {
        return new GeoPoint(record.getDouble("latitude"), record.getDouble("longitude"));
    }

    public static GeoPoint fromXY(double x, double y) {
        return new GeoPoint(y, x);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoPoint aiLatLng = (GeoPoint) o;

        return Double.compare(aiLatLng.latitude, latitude) == 0 && Double.compare(aiLatLng.longitude, longitude) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
