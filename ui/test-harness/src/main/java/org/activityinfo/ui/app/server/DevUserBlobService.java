package org.activityinfo.ui.app.server;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.*;
import com.google.common.collect.Maps;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class DevUserBlobService implements UserBlobService {

    public static final String BLOB_INDEX_KIND = "UserBlob";

    @Override
    public UploadCredentials startUpload(AuthenticatedUser user, BlobMetadata metadata) {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        String uploadUrl = blobstoreService.createUploadUrl("/uploadComplete");

        Map<String, String> fields = Maps.newHashMap();
        fields.put("blobId", metadata.getBlobId().asString());

        return new UploadCredentials(uploadUrl, "POST", fields);
    }

    @Override
    public Response getThumbnail(AuthenticatedUser user, BlobId blobId, int width, int height) {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

    @Override
    public ByteSource getContent(AuthenticatedUser user, final BlobId blobId) {
        return new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
                Entity entity = null;
                try {
                    entity = datastoreService.get(KeyFactory.createKey(BLOB_INDEX_KIND, blobId.asString()));
                } catch (EntityNotFoundException e) {
                    throw new IOException(e);
                }
                BlobKey blobKey = (BlobKey) entity.getProperty("blobKey");
                return new BlobstoreInputStream(blobKey);
            }
        };
    }

    @Override
    public Response serveBlob(AuthenticatedUser user, BlobId blobId) {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

    @Override
    public void put(AuthenticatedUser user,
                    BlobMetadata blobMetadata, ByteSource byteSource) throws IOException {

    }

    @Override
    public OutputStream put(AuthenticatedUser user, BlobId blobId, String contentDisposition, String mimeType) {
        return ByteStreams.nullOutputStream();
    }

}
