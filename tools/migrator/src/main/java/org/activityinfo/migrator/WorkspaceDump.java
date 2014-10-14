package org.activityinfo.migrator;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import java.util.List;

public class WorkspaceDump {

    private int ownerUserId;
    private List<Resource> resources = Lists.newArrayList();
    private ResourceId workspaceId;

    public WorkspaceDump(ResourceId workspaceId) {
        this.workspaceId = workspaceId;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}
