package org.activityinfo.server.endpoint.odk;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.enumerated.EnumItem;
import org.activityinfo.model.type.enumerated.EnumType;

import java.util.List;
import java.util.Map;

import static org.activityinfo.model.legacy.CuidAdapter.attributeField;
import static org.activityinfo.model.legacy.CuidAdapter.getLegacyIdFromCuid;

public class LegacyEnumFieldValueParser implements FieldValueParser {
    final private Map<Integer, EnumItem> values;

    LegacyEnumFieldValueParser(EnumType enumType) {
        values = Maps.newHashMapWithExpectedSize(enumType.getValues().size());

        for (EnumItem value : enumType.getValues()) {
            values.put(getLegacyIdFromCuid(value.getId()), value);
        }
    }

    @Override
    public FieldValue parse(String text) {
        if (text == null) throw new IllegalArgumentException("Malformed Element passed to OdkFieldValueParser.parse()");

        String selected[] = text.split(" ");
        List<ResourceId> resourceIds = Lists.newArrayListWithCapacity(selected.length);

        for (String item : selected) {
            if (item.length() < 1) continue;
            resourceIds.add(attributeField(Integer.parseInt(item)));
        }

        return new ReferenceValue(resourceIds); // ReferenceValue not EnumFieldValue, since that's what the old code did
    }
}
