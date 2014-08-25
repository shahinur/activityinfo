package org.activityinfo.server.endpoint.odk;

import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.image.ImageRowValue;
import org.activityinfo.model.type.image.ImageValue;
import org.w3c.dom.Element;

import static com.google.common.net.MediaType.ANY_IMAGE_TYPE;

class ImageFieldValueParser implements OdkFieldValueParser {
    @Override
    public FieldValue parse(Element element) {
        String text = OdkHelper.extractText(element);

        if (text == null) throw new IllegalArgumentException("Malformed Element passed to OdkFieldValueParser.parse()");

        return new ImageValue(new ImageRowValue(ANY_IMAGE_TYPE.toString(), text, Resources.generateId().asString()));
    }
}
