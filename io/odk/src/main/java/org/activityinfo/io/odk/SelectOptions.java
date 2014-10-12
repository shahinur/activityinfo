package org.activityinfo.io.odk;

import org.activityinfo.model.type.Cardinality;
import org.activityinfo.io.odk.xform.Item;

import java.util.List;

interface SelectOptions {
    Cardinality getCardinality();

    List<Item> getItem();
}
