package org.activityinfo.geoadmin;

import com.vividsolutions.jts.geom.Envelope;
import org.activityinfo.geoadmin.model.Bounds;

public class GeoUtils {

    public static Envelope toEnvelope(Bounds bounds) {
        return new Envelope(bounds.getX1(), bounds.getX2(), bounds.getY1(), bounds.getY2());
    }

    public static Bounds toBounds(Envelope envelope) {
        Bounds bounds = new Bounds();
        bounds.setX1(envelope.getMinX());
        bounds.setY1(envelope.getMinY());
        bounds.setX2(envelope.getMaxX());
        bounds.setY2(envelope.getMaxY());
        return bounds;
    }
}
