package org.activityinfo.server.endpoint.odk;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceValue;
import org.w3c.dom.Element;

class ReferenceFieldValueParser implements OdkFieldValueParser {
    @Override
    public FieldValue parse(Element element) {
        String text = OdkHelper.extractText(element);

        if (text == null) throw new IllegalArgumentException("Malformed Element passed to OdkFieldValueParser.parse()");

        return new ReferenceValue(ResourceId.valueOf(text));
    }
}
