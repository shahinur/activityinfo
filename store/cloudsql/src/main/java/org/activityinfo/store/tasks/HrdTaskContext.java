package org.activityinfo.store.tasks;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.tasks.TaskContext;
import org.activityinfo.store.hrd.HrdResourceStore;
import org.activityinfo.store.hrd.HrdStoreAccessor;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class HrdTaskContext implements TaskContext {

    private final HrdStoreAccessor accessor;
    private HrdResourceStore store;
    private final AuthenticatedUser user;

    public HrdTaskContext(HrdResourceStore store, AuthenticatedUser user) {
        this.store = store;
        this.user = user;
        this.accessor = store.createAccessor(user);
    }

    @Override
    public OutputStream createBlob(BlobId blobId, String filename, String contentType) {
        return new ByteArrayOutputStream();
    }

    @Override
    public UserResource getResource(ResourceId resourceId) throws Exception {
        return store.get(user, resourceId);
    }

    @Override
    public ResourceCursor openCursor(ResourceId formClassId) throws Exception {
        return store.createAccessor(user).openCursor(formClassId);
    }
}
