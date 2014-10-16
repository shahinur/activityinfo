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

import org.activityinfo.model.annotation.RecordBean;
import org.activityinfo.model.annotation.Transient;

import java.io.Serializable;

/*
 * Bounding box for a map. 
 * 
 * This cannot be mapped 1:1 to a rectangle, since a lat/long combination is a coordinate on
 * a sphere as opposed to a coordinate on a 2D plane.
 */
@RecordBean(classId = "_geo:extents")
public class GeoExtents implements Serializable {

    private static final int LAT_MAX = 90;
    private static final int LNG_MAX = 180;
    private static final int LAT_MIN = -LAT_MAX;
    private static final int LNG_MIN = -180;

    private double minLat;
    private double maxLat;
    private double minLon;
    private double maxLon;

    GeoExtents() {

    }

    public GeoExtents(double minLat, double maxLat, double minLon, double maxLon) {
        super();
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLon = minLon;
        this.maxLon = maxLon;
    }

    public GeoExtents(GeoExtents toCopy) {
        super();
        this.minLat = toCopy.minLat;
        this.maxLat = toCopy.maxLat;
        this.minLon = toCopy.minLon;
        this.maxLon = toCopy.maxLon;
    }

    /**
     * @return maximum geographic bounds (-180, -90, 180, 90)s
     */
    public static GeoExtents maxGeoBounds() {
        return new GeoExtents(LAT_MIN, LAT_MAX, LNG_MIN, LNG_MAX);
    }

    public double getMinLat() {
        return minLat;
    }

    public void setMinLat(double minLat) {
        this.minLat = minLat;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public void setMaxLat(double maxLat) {
        this.maxLat = maxLat;
    }

    public double getMinLon() {
        return minLon;
    }

    public void setMinLon(double minLon) {
        this.minLon = minLon;
    }

    public double getMaxLon() {
        return maxLon;
    }

    public void setMaxLon(double maxLon) {
        this.maxLon = maxLon;
    }

    public void grow(double lat, double lng) {
        if (lat < minLat) {
            minLat = lat;
        }
        if (lat > maxLat) {
            maxLat = lat;
        }
        if (lng < minLon) {
            minLon = lng;
        }
        if (lng > maxLon) {
            maxLon = lng;
        }
    }

    /**
     * Calculates the intersection of this Extents with given Extents
     *
     * @param b another Extents with which to intersect this Extents
     * @return the intersection of the two Extentss
     */
    public GeoExtents intersect(GeoExtents b) {
        return new GeoExtents(Math.max(minLat, b.minLat),
                Math.min(maxLat, b.maxLat),
                Math.max(minLon, b.minLon),
                Math.min(maxLon, b.maxLon));
    }

    /**
     * @return true if this Extents intersects with <code>b</code>
     */
    public boolean intersects(GeoExtents b) {
        return !(b.maxLon < minLon || b.minLon > maxLon || b.maxLat < minLat || b.minLat > maxLat);
    }

    public void grow(GeoExtents extents) {

        if (!extents.isEmpty()) {
            grow(extents.minLat, extents.minLon);
            grow(extents.maxLat, extents.maxLon);
        }
    }

    public static GeoExtents emptyExtents() {
        return new GeoExtents(+90.0, -90.0, +180.0, -180.0);
    }

    public static GeoExtents empty() {
        return emptyExtents();
    }

    /**
     * @param b
     * @return true if this Extents contains <code>b</code>
     */
    public boolean contains(GeoExtents b) {
        return b.minLon >= minLon && b.maxLon <= maxLon && b.minLat >= minLat && b.maxLat <= maxLat;
    }

    public boolean contains(GeoPoint center) {
        return contains(center.getLongitude(), center.getLatitude());
    }

    /**
     * @return true if this Extents contains the point at (x,y)
     */
    public boolean contains(double x, double y) {
        return x >= minLon && x <= maxLon && y >= minLat && y <= maxLat;
    }

    public static GeoExtents create(double x1, double y1, double x2, double y2) {
        return new GeoExtents(y1, y2, x1, x2);
    }

    /**
     * @return the x (longitude) coordinate of the Extents's centroid, (x1+x2)/2
     */
    @Transient
    public double getCenterX() {
        return (minLon + maxLon) / 2;
    }

    /**
     * @return the y (latitudinal) coordinate of the Extents's centroid,
     * (y1+y2)/2
     */
    @Transient
    public double getCenterY() {
        return (minLat + maxLat) / 2;
    }

    @Transient
    public boolean isEmpty() {
        return minLat > maxLat || minLon > maxLon;
    }

    public GeoPoint center() {
        return new GeoPoint((minLat + maxLat) / 2.0, (minLon + maxLon) / 2.0);
    }

    @Override
    public int hashCode() {
        return (minLon + "").hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GeoExtents other = (GeoExtents) obj;
        return minLat == other.minLat &&
               maxLat == other.maxLat &&
               minLon == other.minLon &&
               maxLon == other.maxLon;
    }

    @Override
    public String toString() {
        return "Extents{" +
               "minLat=" + minLat +
               ", maxLat=" + maxLat +
               ", minLon=" + minLon +
               ", maxLon=" + maxLon +
               '}';
    }

}
