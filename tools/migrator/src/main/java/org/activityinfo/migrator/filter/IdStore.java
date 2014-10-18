package org.activityinfo.migrator.filter;

import com.google.common.base.Optional;
import org.activityinfo.model.resource.ResourceId;

public interface IdStore {

    Optional<ResourceId> getNewId(ResourceId oldId);

    void putNewId(ResourceId oldId, ResourceId newId);
}
