package org.activityinfo.service.blob;

import com.google.common.io.ByteSource;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;

import java.net.URI;

public class TestBlobFieldStorageService implements BlobFieldStorageService {
    @Override
    public UploadCredentials getUploadCredentials(ResourceId userId, BlobId blobId) {
        return null;
    }

    @Override
    public URI getBlobUrl(BlobId blobId) {
        return null;
    }

    @Override
    public void put(AuthenticatedUser authenticatedUser, String contentDisposition, String mimeType, BlobId blobId,
                    ByteSource byteSource) {
    }
}
