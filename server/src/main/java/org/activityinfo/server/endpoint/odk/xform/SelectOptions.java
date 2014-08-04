package org.activityinfo.server.endpoint.odk.xform;

import org.activityinfo.model.type.Cardinality;

import java.util.List;

interface SelectOptions {
    Cardinality getCardinality();

    List<Item> getItem();
}
