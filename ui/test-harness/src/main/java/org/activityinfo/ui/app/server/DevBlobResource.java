package org.activityinfo.ui.app.server;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.datastore.*;
import com.google.common.io.ByteSource;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobResource;

import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

public class DevBlobResource implements BlobResource {

    public static final String BLOB_INDEX_KIND = "UserBlob";
    private BlobId blobId;

    public DevBlobResource(BlobId blobId) {

        this.blobId = blobId;
    }

    @Override
    public Response getThumbnail(@QueryParam("width") int width, @QueryParam("height") int height) {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

    @Override
    public ByteSource getContent() {
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
    public Response get() {
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }
}
