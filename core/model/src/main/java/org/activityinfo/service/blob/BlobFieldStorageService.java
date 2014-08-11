package org.activityinfo.service.blob;

import org.activityinfo.model.resource.ResourceId;

import java.net.URI;

/**
 * Provides storage for fields which have blob values, such as images
 * or general attachment fields.
 */
public interface BlobFieldStorageService {

    /**
     * Provides the upload credentials for a user to upload a blob.
     *
     * @param blobId
     * @return the
     */
    UploadCredentials getUploadCredentials(ResourceId userId, BlobId blobId);

    /**
     * Provides a temporary, signed URL via which the user can access a blob
     * associated with a field value.
     * @param blobId
     * @return
     */
    URI getBlobUrl(BlobId blobId);

}
