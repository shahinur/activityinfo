package org.activityinfo.ui.app.server;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.blob.BlobFieldStorageService;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobResource;
import org.activityinfo.service.blob.UploadCredentials;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Path("/service/blob")
public class DevBlobStorageService implements BlobFieldStorageService {

    @POST
    @Override
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public UploadCredentials startUpload(@InjectParam AuthenticatedUser user,
                                         @FormParam("blobId") BlobId blobId,
                                         @FormParam("fileName") String fileName) {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        String uploadUrl = blobstoreService.createUploadUrl("/uploadComplete");

        Map<String, String> fields = Maps.newHashMap();
        fields.put("blobId", blobId.asString());

        return new UploadCredentials(uploadUrl, "POST", fields);

    }

    @Override
    public void put(AuthenticatedUser user, BlobId blobId, String contentDisposition, String mimeType, ByteSource byteSource) throws IOException {

    }

    @Override
    public OutputStream put(AuthenticatedUser user, BlobId blobId, String contentDisposition, String mimeType) {
        return ByteStreams.nullOutputStream();
    }

    @Override
    public BlobResource getBlob(@InjectParam AuthenticatedUser user, @PathParam("blobId") BlobId blobId) {
        return new DevBlobResource(blobId);
    }

}
