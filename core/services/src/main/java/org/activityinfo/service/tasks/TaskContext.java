package org.activityinfo.service.tasks;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.ResourceCursor;

import java.io.OutputStream;

public interface TaskContext {

    /**
     * Creates a new blob object owned by the task's owner
     *
     * @param blobId the generated blobId
     * @return an OutputStream to which the blob can be written.
     */
    OutputStream createBlob(BlobId blobId, String filename, String contentType);

    UserResource getResource(ResourceId resourceId) throws Exception;

    ResourceCursor openCursor(ResourceId formClassId) throws Exception;

}
