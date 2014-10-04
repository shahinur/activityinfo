package org.activityinfo.service.blob;

import com.google.common.io.ByteSource;
import org.activityinfo.model.auth.AuthenticatedUser;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Provides storage for fields which have blob values, such as images
 * or general attachment fields.
 */
@Path("/service/blob")
public interface UserBlobService {


    /**
     * Creates the metadata record for the blob identified by {@code blobId} and
     * provides a signed URL and a set of credentials that will allow the user
     * to upload the contents of the blob directly to GCS.
     *
     * @param user user on whose behalf the blob is to be uploaded.
     * @param metadata {@link org.activityinfo.service.blob.BlobMetadata} object describing the blob to be uploaded.
     * @return credentials that the client can use to upload the blob to the server.
     */
    public UploadCredentials startUpload(AuthenticatedUser user, BlobMetadata metadata);


    /**
     * Creates a metadata record for the new {@code blobId} and writes
     * the content directly to the blob store.
     * @param user the user on whose behalf the blob is to be stored
     * @param blobMetadata the metadata which describes the blob
     * @param byteSource the content of the blob
     * @throws IOException
     */
    public void put(
        AuthenticatedUser user,
        BlobMetadata blobMetadata,
        ByteSource byteSource) throws IOException;

    /**
     * Provides an OutputStream to which results can be written. The metadata
     * record is written upon closing of the stream.
     * @param user
     * @param blobId
     * @param contentDisposition
     * @param mimeType
     * @return
     */
    public OutputStream put(
        AuthenticatedUser user,
        BlobId blobId,
        String contentDisposition,
        String mimeType) throws IOException;


    /**
     * Serves a thumbnail of the given blob with the given width and height.
     * @param blobId
     * @param width the desired image width in pixels
     * @param height the desired image height in pixels
     * @return a JAX-RS {@link javax.ws.rs.core.Response} serving the image
     */
    Response getThumbnail(AuthenticatedUser user, BlobId blobId, int width, int height);


    /**
     *  Retrieves the content of a user blob.
     *
     * @return the content of the blob as a {@code ByteSource}
     */
    ByteSource getContent(AuthenticatedUser user, BlobId blobId);


    /**
     * Redirects the client to a temporary, signed URL via which the user can access this blob
     */
    Response serveBlob(AuthenticatedUser user, BlobId blobId);


}
