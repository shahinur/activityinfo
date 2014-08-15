package org.activityinfo.service.blob;

import com.google.common.io.ByteSource;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;

import java.io.IOException;
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

    /**
     * Uploads a blob with the specified id to GCS
     * @param authenticatedUser
     * @param blobId
     * @param byteSource
     * @throws IOException
     */
    void put(AuthenticatedUser authenticatedUser, BlobId blobId, ByteSource byteSource) throws IOException;
}
