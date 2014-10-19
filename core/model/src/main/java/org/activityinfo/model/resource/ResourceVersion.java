package org.activityinfo.model.resource;

import java.util.Date;


public class ResourceVersion {
    private ResourceId resourceId;
    private Date dateCommitted;
    private long version;
    private int userId;

    public ResourceId getResourceId() {
        return resourceId;
    }

    public void setResourceId(ResourceId resourceId) {
        this.resourceId = resourceId;
    }

    public Date getDateCommitted() {
        return dateCommitted;
    }

    public void setDateCommitted(Date dateCommitted) {
        this.dateCommitted = dateCommitted;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
