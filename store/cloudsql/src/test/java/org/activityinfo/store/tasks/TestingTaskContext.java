package org.activityinfo.store.tasks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.tasks.BlobResult;
import org.activityinfo.service.tasks.TaskContext;
import org.activityinfo.store.hrd.TestingEnvironment;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class TestingTaskContext implements TaskContext {

    Map<BlobId, ByteArrayOutputStream> blobs = Maps.newHashMap();
    List<BlobResult> blobResults = Lists.newArrayList();
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
        blobResults.add(new BlobResult(blobId, filename));
        return baos;
    }

    @Override
    public UserResource getResource(ResourceId resourceId) throws Exception {
        return environment.getStore().get(environment.getUser(), resourceId);
    }

    @Override
    public ResourceCursor openCursor(ResourceId formClassId) throws Exception {
        return environment.getStore().createAccessor(environment.getUser()).openCursor(formClassId);
    }

    public List<BlobResult> getBlobResults() {
        return blobResults;
    }

    public ByteSource getBlob(BlobId blobId) {
        return ByteSource.wrap(blobs.get(blobId).toByteArray());
    }

}
