package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.server.endpoint.odk.xform.Item;

import java.util.List;

class ReferenceTypeSelectOptions implements SelectOptions {
    final private Cardinality cardinality;
    final private List<Item> item;

    ReferenceTypeSelectOptions(ReferenceType referenceType) {
        cardinality = referenceType.getCardinality();
        item = Lists.newArrayListWithCapacity(referenceType.getRange().size());
        for (ResourceId resourceId : referenceType.getRange()) {
            Item item = new Item();
            item.label = "";    //TODO Set label to correct value
            item.value = resourceId.asString();
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
