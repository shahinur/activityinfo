package org.activityinfo.store.tasks;

import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.blob.BlobFieldStorageService;
import org.activityinfo.service.tasks.TaskContext;
import org.activityinfo.store.hrd.HrdResourceStore;

public class HrdTaskContextProvider implements TaskContextProvider {
    private HrdResourceStore store;
    private BlobFieldStorageService blobService;

    @Inject
    public HrdTaskContextProvider(HrdResourceStore store, BlobFieldStorageService blobService) {
        this.store = store;
        this.blobService = blobService;
    }

    @Override
    public TaskContext create(AuthenticatedUser user) {
        return new HrdTaskContext(store, blobService, user);
    }
}
