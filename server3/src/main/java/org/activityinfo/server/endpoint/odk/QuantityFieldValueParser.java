package org.activityinfo.server.endpoint.odk;

import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.number.QuantityType;
import org.w3c.dom.Element;

class QuantityFieldValueParser implements OdkFieldValueParser {
    final private String units;

    QuantityFieldValueParser(QuantityType quantityType) {
        this.units = quantityType.getUnits();
    }

    @Override
    public FieldValue parse(Element element) {
        double value;
        String text = OdkHelper.extractText(element);

        if (text == null) throw new IllegalArgumentException("Malformed Element passed to OdkFieldValueParser.parse()");

        try {
            value = Double.parseDouble(text);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unparsable double in Element passed to OdkFieldValueParser.parse()", e);
        }

        return new Quantity(value, units);
    }
}
