package org.activityinfo.server.endpoint.odk;

import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.NarrativeValue;
import org.w3c.dom.Element;

class NarrativeFieldValueParser implements OdkFieldValueParser {
    @Override
    public FieldValue parse(Element element) {
        return NarrativeValue.valueOf(OdkHelper.extractText(element));
    }
}
