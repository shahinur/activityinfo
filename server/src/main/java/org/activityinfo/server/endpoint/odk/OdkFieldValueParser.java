package org.activityinfo.server.endpoint.odk;

import org.activityinfo.model.type.FieldValue;
import org.w3c.dom.Element;

public interface OdkFieldValueParser {
    public FieldValue parse(Element element);
}
