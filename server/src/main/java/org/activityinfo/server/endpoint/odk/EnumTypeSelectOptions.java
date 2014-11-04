package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Lists;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.server.endpoint.odk.xform.Item;

import java.util.List;

class EnumTypeSelectOptions implements SelectOptions {
    final private Cardinality cardinality;
    final private List<Item> item;

    EnumTypeSelectOptions(EnumType enumType) {
        cardinality = enumType.getCardinality();
        item = Lists.newArrayListWithCapacity(enumType.getValues().size());
        for (EnumValue enumValue : enumType.getValues()) {
            Item item = new Item();
            item.label = enumValue.getLabel();
            item.value = enumValue.getId().asString();
            this.item.add(item);
        }
    }

    @Override
    public Cardinality getCardinality() {
        return cardinality;
    }

    @Override
    public List<Item> getItem() {
        return item;
    }
}
