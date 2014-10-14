package org.activityinfo.store.hrd.entity.workspace;

import org.activityinfo.model.resource.ResourceId;

public interface ResourceVersion {

    ResourceId getResourceId();

    ResourceId getOwnerId();

    long getVersion();



}
