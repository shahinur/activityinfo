package org.activityinfo.io.load;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.StoreLoader;

public class LoadContext {

    private final StoreLoader loader;
    private final ResourceId parentId;

    public LoadContext(StoreLoader loader, ResourceId parentId) {
        this.loader = loader;
        this.parentId = parentId;
    }

    public StoreLoader getStoreLoader() {
        return loader;
    }

    public ResourceId getParentId() {
        return parentId;
    }
}
