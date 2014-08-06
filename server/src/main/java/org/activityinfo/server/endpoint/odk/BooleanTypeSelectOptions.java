package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Lists;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.server.endpoint.odk.xform.Item;

import java.util.List;

class BooleanTypeSelectOptions implements SelectOptions {
    final private List<Item> item;

    BooleanTypeSelectOptions() {
        Item no = new Item();
        no.label = "no";
        no.value = "false";
        Item yes = new Item();
        yes.label = "yes";
        yes.value = "true";
        item = Lists.newArrayList(yes, no);
    }

    @Override
    public Cardinality getCardinality() {
        return Cardinality.SINGLE;
    }

    @Override
    public List<Item> getItem() {
        return item;
    }
}
