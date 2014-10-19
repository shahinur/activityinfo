package org.activityinfo.service.tasks.appengine;

import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobMetadata;
import org.activityinfo.service.store.StoreLoader;
import org.activityinfo.service.store.StoreReader;
import org.activityinfo.service.tasks.TaskContext;
import org.activityinfo.store.hrd.TestingEnvironment;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;

public class TestingTaskContext implements TaskContext {

    Map<BlobId, ByteArrayOutputStream> blobs = Maps.newHashMap();
    TestingEnvironment environment;

    public TestingTaskContext(TestingEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public OutputStream createBlob(BlobId blobId, String filename, String contentType) {
        if(blobs.containsKey(blobId)) {
            throw new IllegalStateException("Blob already created");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        blobs.put(blobId, baos);
        return baos;
    }

    @Override
    public UserResource getResource(ResourceId resourceId) throws Exception {
        return environment.getStore().get(environment.getUser(), resourceId);
    }

    @Override
    public StoreReader openStoreReader() throws Exception {
        return environment.getStore().openReader(environment.getUser());
    }

    @Override
    public StoreLoader beginLoad(ResourceId parentId) throws Exception {
        return environment.getStore().beginLoad(environment.getUser(), parentId);
    }

    public ByteSource getBlob(BlobId blobId) {
        return ByteSource.wrap(blobs.get(blobId).toByteArray());
    }

    @Override
    public BlobMetadata getBlobMetadata(BlobId blobId) {
        throw new UnsupportedOperationException();
    }

}
