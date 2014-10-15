package org.activityinfo.service.tasks.appengine;

import com.google.common.io.ByteSource;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobMetadata;
import org.activityinfo.service.blob.UserBlobService;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.StoreLoader;
import org.activityinfo.service.store.StoreReader;
import org.activityinfo.service.tasks.TaskContext;

import java.io.IOException;
import java.io.OutputStream;

public class AppEngineTaskContext implements TaskContext {

    private final StoreReader storeReader;
    private ResourceStore store;
    private UserBlobService blobService;
    private final AuthenticatedUser user;

    public AppEngineTaskContext(ResourceStore store, UserBlobService blobService, AuthenticatedUser user) {
        this.store = store;
        this.blobService = blobService;
        this.user = user;
        this.storeReader = store.openReader(user);
    }

    @Override
    public OutputStream createBlob(BlobId blobId, String filename, String contentType) throws IOException {
        return blobService.put(user, blobId, filename, contentType);
    }

    @Override
    public UserResource getResource(ResourceId resourceId) throws Exception {
        return store.get(user, resourceId);
    }

    @Override
    public ResourceCursor openCursor(ResourceId formClassId) throws Exception {
        return storeReader.openCursor(formClassId);
    }

    @Override
    public StoreLoader beginLoad(ResourceId parentId) throws Exception {
        return store.beginLoad(user, parentId);
    }

    @Override
    public ByteSource getBlob(BlobId blobId) {
        return blobService.getContent(user, blobId);
    }

    @Override
    public BlobMetadata getBlobMetadata(BlobId blobId) {
        return blobService.getBlobMetadata(user, blobId);
    }
}
