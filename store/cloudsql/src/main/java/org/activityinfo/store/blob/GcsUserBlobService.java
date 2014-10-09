package org.activityinfo.store.blob;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.common.base.Optional;
import com.google.common.io.ByteSource;
import com.google.inject.Inject;
import com.sun.jersey.core.header.ContentDisposition;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobMetadata;
import org.activityinfo.service.blob.UploadCredentials;
import org.activityinfo.service.blob.UserBlobService;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

public class GcsUserBlobService implements UserBlobService {

    private static final Logger LOGGER = Logger.getLogger(GcsUserBlobService.class.getName());

    private final AppIdentityService appIdentityService = AppIdentityServiceFactory.getAppIdentityService();

    private final String bucketName;

    @Inject
    public GcsUserBlobService(DeploymentConfiguration config) {
        this.bucketName = config.getBlobServiceBucketName();

        LOGGER.info("Service account: " + this.appIdentityService.getServiceAccountName());
    }

    /**
     * Retrieves a {@code UserBlob} from the metadata store and asserts that {@code user}
     * is authorized to view the blob.
     *
     * @param user requesting user
     * @param blobId the id of the blob
     */
    private UserBlob getBlobAndAssertAuthorized(AuthenticatedUser user, BlobId blobId) {

        Optional<UserBlob> metadata = MetadataTransaction.getUserBlob(blobId);
        if(!metadata.isPresent()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if(metadata.get().getUserId() != user.getId()) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
        return metadata.get();
    }

    /**
     * Ensures that this request does not overwrite an existing resource created by another user, and then
     * creates a new metadata instance for the record.
     *
     * @param user
     * @param blobMetadata
     * @return
     */
    private UserBlob assertAuthorizedAndCreateNew(AuthenticatedUser user, BlobMetadata blobMetadata) {

        UserBlob newBlob = UserBlob.newBlob(user, blobMetadata);

        try(MetadataTransaction tx = new MetadataTransaction()) {
            Optional<UserBlob> metadata = tx.get(blobMetadata.getBlobId());
            if(metadata.isPresent()) {
                // A user may try several times to upload the same blob, so if a metadata record
                // already exists, just verify that it's not another user trying to overwrite the blob,
                // and that this not a different blob with the same id

                if(metadata.get().getUserId() != user.getId()) {
                    throw new WebApplicationException(Response.Status.FORBIDDEN);
                } else if(!metadata.get().getMetadata().equals(blobMetadata)) {
                    throw new WebApplicationException(Response.Status.CONFLICT);
                }
            }

            tx.put(newBlob);
            tx.commit();
        }

        return newBlob;
    }


    /**
     * Creates a UserBlobStorageObject wrapper for the given UserBlob
     */
    private UserBlobStorageObject getStorageObject(UserBlob userBlob) {
        return new UserBlobStorageObject(bucketName, userBlob);
    }

    private UserBlobStorageObject getStorageObjectAndAssertAuthorized(AuthenticatedUser user, BlobId blobId) {
        return getStorageObject(getBlobAndAssertAuthorized(user, blobId));
    }

    @Override
    public void put(AuthenticatedUser user,
                    BlobMetadata blobMetadata,
                    ByteSource byteSource) throws IOException {

        UserBlobStorageObject object = getStorageObject(assertAuthorizedAndCreateNew(user, blobMetadata));
        try(OutputStream out = object.openOutputStream()) {
            byteSource.copyTo(out);
        }
    }

    @Override
    public OutputStream put(final AuthenticatedUser user,
                            final BlobId blobId,
                            final String contentDisposition,
                            final String mimeType) throws IOException {

        BlobMetadata blobMetadata;
        try {
            blobMetadata = new BlobMetadata(blobId, new ContentDisposition(contentDisposition),
                MediaType.valueOf(mimeType));

        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid content disposition: " + contentDisposition, e);
        }

        UserBlobStorageObject storageObject = getStorageObject(assertAuthorizedAndCreateNew(user, blobMetadata));
        return storageObject.openOutputStream();
    }


    @Override
    public UploadCredentials startUpload(AuthenticatedUser user, BlobMetadata metadata) {

        LOGGER.log(Level.INFO, String.format("Upload request: user = %s,  metadata = %s\n",
            user, metadata.toString()));

        if (user == null || user.isAnonymous()) {
            throw new WebApplicationException(UNAUTHORIZED);
        }

        UserBlobStorageObject userBlob = getStorageObject(assertAuthorizedAndCreateNew(user, metadata));
        return userBlob.getUploadCredentials();
    }

    @Override
    public ByteSource getContent(AuthenticatedUser user, final BlobId blobId) {
        return getStorageObjectAndAssertAuthorized(user, blobId).asByteSource();
    }

    @Override
    public BlobMetadata getBlobMetadata(AuthenticatedUser user, BlobId blobId) {
        return getBlobAndAssertAuthorized(user, blobId).getMetadata();
    }

    @Override
    public Response serveBlob(AuthenticatedUser user, BlobId blobId) {
        URI signedUrl = getStorageObjectAndAssertAuthorized(user, blobId)
            .getSignedUrl();

        return Response.temporaryRedirect(signedUrl).build();
    }

    @Override
    public Response getThumbnail(AuthenticatedUser user, BlobId blobId, int width, int height) {

        UserBlobStorageObject blob = getStorageObjectAndAssertAuthorized(user, blobId);

        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        Image image = ImagesServiceFactory.makeImageFromBlob(blob.getBlobstoreKey());
        Transform resize = ImagesServiceFactory.makeResize(width, height);
        Image newImage = imagesService.applyTransform(resize, image);

        return Response.ok(newImage.getImageData()).build();
    }

}
