package org.activityinfo.server.endpoint.odk;

import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.NarrativeValue;
import org.w3c.dom.Element;

class NarrativeFieldValueParser implements OdkFieldValueParser {
    @Override
    public FieldValue parse(Element element) {
        String text = OdkHelper.extractText(element);

        if (text == null) throw new IllegalArgumentException("Malformed Element passed to OdkFieldValueParser.parse()");

        return new NarrativeValue(text);
    }
}
