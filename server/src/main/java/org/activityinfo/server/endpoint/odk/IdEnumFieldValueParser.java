package org.activityinfo.server.endpoint.odk;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumItem;
import org.activityinfo.model.type.enumerated.EnumType;

import java.util.List;
import java.util.Map;

class IdEnumFieldValueParser implements FieldValueParser {
    final private Cardinality cardinality;
    final private Map<ResourceId, EnumItem> values;

    IdEnumFieldValueParser(EnumType enumType) {
        this.cardinality = enumType.getCardinality();
        values = Maps.newHashMapWithExpectedSize(enumType.getValues().size());

        for (EnumItem value : enumType.getValues()) {
            values.put(value.getId(), value);
        }
    }

    @Override
    public FieldValue parse(String text) {
        if (text == null) throw new IllegalArgumentException("Malformed Element passed to OdkFieldValueParser.parse()");

        switch (cardinality) {
            default:
                String selected[] = text.split(" ");
                List<ResourceId> resourceIds = Lists.newArrayListWithCapacity(selected.length);

                for (String item : selected) {
                    if (item.length() < 1) continue;
                    resourceIds.add(ResourceId.valueOf(item));
                }

                return new EnumFieldValue(resourceIds);

            case SINGLE:
                return values.get(ResourceId.valueOf(text));
        }
    }
}
