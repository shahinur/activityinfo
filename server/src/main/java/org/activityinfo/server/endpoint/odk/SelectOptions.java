package org.activityinfo.server.endpoint.odk;

import org.activityinfo.model.type.Cardinality;
import org.activityinfo.server.endpoint.odk.xform.Item;

import java.util.List;

interface SelectOptions {
    Cardinality getCardinality();

    List<Item> getItem();
}
