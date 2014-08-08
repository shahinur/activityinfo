package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Maps;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.w3c.dom.Element;

import java.util.Map;

class EnumFieldValueParser implements OdkFieldValueParser {
    final private Map<ResourceId, EnumValue> values;

    EnumFieldValueParser(EnumType enumType) {
        values = Maps.newHashMapWithExpectedSize(enumType.getValues().size());

        for (EnumValue value : enumType.getValues()) {
            values.put(value.getId(), value);
        }
    }

    @Override
    public FieldValue parse(Element element) {
        String text = OdkHelper.extractText(element);

        if (text == null) throw new IllegalArgumentException("Malformed Element passed to OdkFieldValueParser.parse()");

        return values.get(ResourceId.create(text));
    }
}
