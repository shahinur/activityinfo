package org.activityinfo.server.endpoint.odk;

import com.google.common.io.ByteSource;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.blob.BlobFieldStorageService;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobResource;
import org.activityinfo.service.blob.UploadCredentials;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class TestBlobFieldStorageService implements BlobFieldStorageService {

    @Override
    public UploadCredentials startUpload(@InjectParam AuthenticatedUser user,
                                         @PathParam("blobId") BlobId blobId, @FormParam("fileName") String fileName) {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

    @Override
    public void put(AuthenticatedUser user, BlobId blobId, String contentDisposition, String mimeType, ByteSource byteSource) throws IOException {

    }

    @Override
    public BlobResource getBlob(@InjectParam AuthenticatedUser user, @PathParam("blobId") BlobId blobId) {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

}
