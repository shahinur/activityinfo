package org.activityinfo.server.endpoint.odk;

import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.geo.GeoPoint;
import org.w3c.dom.Element;

class GeoPointFieldValueParser implements OdkFieldValueParser {
    @Override
    public FieldValue parse(Element element) {
        double latitude, longitude;
        String text = OdkHelper.extractText(element);

        if (text == null) throw new IllegalArgumentException("Malformed Element passed to OdkFieldValueParser.parse()");

        String coords[] = text.split("\\s+");
        if (coords.length < 2) {
            throw new IllegalArgumentException("Insufficient doubles in Element passed to OdkFieldValueParser.parse()");
        }

        try {
            latitude = Double.parseDouble(coords[0]);
            longitude = Double.parseDouble(coords[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unparsable double in Element passed to OdkFieldValueParser.parse()", e);
        }

        return new GeoPoint(latitude, longitude);
    }
}
