package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Lists;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.server.endpoint.odk.xform.Item;
import org.activityinfo.service.lookup.ReferenceChoice;
import org.activityinfo.service.lookup.ReferenceProvider;

import java.util.List;

class ReferenceTypeSelectOptions implements SelectOptions {
    final private Cardinality cardinality;
    final private List<Item> item;

    ReferenceTypeSelectOptions(ReferenceType referenceType, ReferenceProvider referenceProvider) {
        cardinality = referenceType.getCardinality();
        item = Lists.newArrayList();

        for (ReferenceChoice choice : referenceProvider.getChoices(referenceType.getRange())) {
            Item item = new Item();
            item.label = choice.getLabel();
            item.value = choice.getId().asString();
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
