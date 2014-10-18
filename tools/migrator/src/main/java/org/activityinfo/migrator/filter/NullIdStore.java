package org.activityinfo.migrator.filter;

import com.google.common.base.Optional;
import org.activityinfo.model.resource.ResourceId;

public class NullIdStore implements IdStore {
    @Override
    public Optional<ResourceId> getNewId(ResourceId oldId) {
        return Optional.absent();
    }

    @Override
    public void putNewId(ResourceId oldId, ResourceId newId) {

    }
}
