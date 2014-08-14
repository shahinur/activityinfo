package org.activityinfo.server.endpoint.odk;

import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.image.ImageValue;
import org.w3c.dom.Element;

class ImageFieldValueParser implements OdkFieldValueParser {
    @Override
    public FieldValue parse(Element element) {
        String code = OdkHelper.extractText(element);

        if (code == null) throw new IllegalArgumentException("Malformed Element passed to OdkFieldValueParser.parse()");

        return new ImageValue();
    }
}
