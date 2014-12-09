package org.activityinfo.service.tasks;

import com.google.common.io.ByteSource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobMetadata;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.store.StoreLoader;

import java.io.IOException;
import java.io.OutputStream;

public interface TaskContext {

    /**
     * Creates a new blob object owned by the task's owner
     *
     * @param blobId the generated blobId
     * @return an OutputStream to which the blob can be written.
     */
    OutputStream createBlob(BlobId blobId, String filename, String contentType) throws IOException;

    UserResource getResource(ResourceId resourceId) throws Exception;

    ResourceCursor openCursor(ResourceId formClassId) throws Exception;

    StoreLoader beginLoad(ResourceId parentId) throws Exception;

    ByteSource getBlob(BlobId blobId);

    BlobMetadata getBlobMetadata(BlobId blobId);
}
