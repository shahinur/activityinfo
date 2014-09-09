package org.activityinfo.model.analysis;

import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

public class PivotTableModel implements IsResource {

    private final ResourceId resourceId;
    private final ResourceId ownerId;

    public PivotTableModel(ResourceId id, ResourceId ownerId) {
        this.resourceId = id;
        this.ownerId = ownerId;
    }

    @Override
    public ResourceId getId() {
        return resourceId;
    }

    @Override
    public Resource asResource() {
        return null;
    }


}
