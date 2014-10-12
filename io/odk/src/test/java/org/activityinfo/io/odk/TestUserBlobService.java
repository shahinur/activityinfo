package org.activityinfo.io.odk;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobMetadata;
import org.activityinfo.service.blob.UploadCredentials;
import org.activityinfo.service.blob.UserBlobService;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;

public class TestUserBlobService implements UserBlobService {

    @Override
    public UploadCredentials startUpload(AuthenticatedUser user, BlobMetadata metadata) {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

    @Override
    public Response getThumbnail(AuthenticatedUser user, BlobId blobId, int width, int height) {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

    @Override
    public ByteSource getContent(AuthenticatedUser user, BlobId blobId) {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

    @Override
    public BlobMetadata getBlobMetadata(AuthenticatedUser user, BlobId blobId) {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

    @Override
    public Response serveBlob(AuthenticatedUser user, BlobId blobId) {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

    @Override
    public void put(AuthenticatedUser user, BlobMetadata blobMetadata, ByteSource byteSource) throws IOException {
    }

    @Override
    public OutputStream put(AuthenticatedUser user, BlobId blobId, String contentDisposition, String mimeType) {
        return ByteStreams.nullOutputStream();
    }
}