package org.activityinfo.server.endpoint.odk;

import com.google.common.base.Strings;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.time.LocalDate;
import org.w3c.dom.Element;

class LocalDateFieldValueParser implements OdkFieldValueParser {
    @Override
    public FieldValue parse(Element element) {
        String text = OdkHelper.extractText(element);

        if (Strings.isNullOrEmpty(text)) {
            return null;
        } else {
            if (text.contains("T")) {
                text = text.substring(0, text.indexOf("T"));
            }
            return LocalDate.parse(text);
        }
    }
}
