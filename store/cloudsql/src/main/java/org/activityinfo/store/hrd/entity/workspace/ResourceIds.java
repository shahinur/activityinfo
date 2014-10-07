package org.activityinfo.store.hrd.entity.workspace;

import org.activityinfo.model.resource.ResourceId;

public class ResourceIds {

    public static ResourceId valueOf(Object propertyValue) {
        if(propertyValue instanceof String) {
            return ResourceId.valueOf((String) propertyValue);
        } else {
            return null;
        }
    }
}
