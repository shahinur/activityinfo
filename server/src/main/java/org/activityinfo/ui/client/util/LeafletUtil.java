package org.activityinfo.ui.client.util;

import org.activityinfo.model.type.geo.GeoExtents;
import org.activityinfo.model.type.geo.GeoPoint;
import org.discotools.gwt.leaflet.client.types.LatLng;
import org.discotools.gwt.leaflet.client.types.LatLngBounds;

public class LeafletUtil {

    public static LatLngBounds newLatLngBounds(GeoExtents bounds) {
        LatLng southWest = new LatLng(bounds.getMinLat(), bounds.getMinLon());
        LatLng northEast = new LatLng(bounds.getMaxLat(), bounds.getMaxLon());
        return new LatLngBounds(southWest, northEast);
    }

    public static LatLng to(GeoPoint latLng) {
        return new LatLng(latLng.getLatitude(), latLng.getLongitude());
    }

    public static String color(String color) {
        if (color == null) {
            return "#FF0000";
        } else if (color.startsWith("#")) {
            return color;
        } else {
            return "#" + color;
        }
    }
}
