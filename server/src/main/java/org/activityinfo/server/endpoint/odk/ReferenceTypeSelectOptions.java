package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Lists;
import org.activityinfo.model.table.InstanceLabelTable;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.server.endpoint.odk.xform.Item;

import java.util.List;

import static com.google.common.collect.Iterables.getOnlyElement;

class ReferenceTypeSelectOptions implements SelectOptions {
    final private Cardinality cardinality;
    final private List<Item> item;

    ReferenceTypeSelectOptions(InstanceTableProvider labelProvider, ReferenceType referenceType) {
        cardinality = referenceType.getCardinality();
        item = Lists.newArrayList();

        InstanceLabelTable data = labelProvider.getTable(getOnlyElement(referenceType.getRange()));

        for (int i = 0; i < data.getNumRows(); i++) {
            Item item = new Item();
            item.label = data.getLabel(i);
            item.value = data.getId(i).asString();
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
