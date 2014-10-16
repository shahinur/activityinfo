package org.activityinfo.model.record;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Types;

class Tags {


    public static boolean has(Record record, ResourceId resourceId) {
        return record.has(propertyName(resourceId));
    }

    public static ResourceId getReference(Record record, ResourceId resourceId) {
        return Types.readReference(record, propertyName(resourceId));
    }

    private static String propertyName(ResourceId resourceId) {
        return "#" + resourceId.asString();
    }

}
