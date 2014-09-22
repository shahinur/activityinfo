package org.activityinfo.ui.app.server;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.*;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.blob.BlobFieldStorageService;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.UploadCredentials;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

@Path("/service/blob")
public class TestBlobFieldStorageService implements BlobFieldStorageService {

    public static final String BLOB_INDEX_KIND = "UserBlob";

    @Override
    public URI getBlobUrl(BlobId blobId) {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

    @Override
    public void put(AuthenticatedUser authenticatedUser, String contentDisposition, String mimeType, BlobId blobId, ByteSource byteSource) throws IOException {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);

    }

    @Override
    public Response getImage(@InjectParam AuthenticatedUser user, @PathParam("resourceId") ResourceId resourceId, @PathParam("fieldId") ResourceId fieldId, @PathParam("blobId") BlobId blobId) throws IOException {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

    @Override
    public Response getThumbnail(@InjectParam AuthenticatedUser user, @PathParam("resourceId") ResourceId resourceId, @PathParam("fieldId") ResourceId fieldId, @PathParam("blobId") BlobId blobId, @QueryParam("width") int width, @QueryParam("height") int height) {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

    @POST
    @Path("credentials/{blobId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public UploadCredentials getUploadCredentials(@InjectParam AuthenticatedUser user, @PathParam("blobId") BlobId blobId) {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        String uploadUrl = blobstoreService.createUploadUrl("/uploadComplete");

        Map<String, String> fields = Maps.newHashMap();
        fields.put("blobId", blobId.asString());

        return new UploadCredentials(uploadUrl, "POST", fields);
    }

    @Override
    public ByteSource getContent(final BlobId blobId) {
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
}
