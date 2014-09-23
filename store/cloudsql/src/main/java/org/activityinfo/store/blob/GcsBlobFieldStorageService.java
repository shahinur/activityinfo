package org.activityinfo.store.blob;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.datastore.*;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
import com.google.inject.Inject;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.service.blob.BlobFieldStorageService;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobResource;
import org.activityinfo.service.blob.UploadCredentials;
import org.activityinfo.store.BadRequestException;
import org.joda.time.Period;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.logging.Logger;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

public class GcsBlobFieldStorageService implements BlobFieldStorageService {

    private static final String KIND = "UserBlob";

    private static final Logger LOGGER = Logger.getLogger(GcsBlobFieldStorageService.class.getName());

    private final AppIdentityService appIdentityService = AppIdentityServiceFactory.getAppIdentityService();
    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    private final String bucketName;

    @Inject
    public GcsBlobFieldStorageService(DeploymentConfiguration config) {
        this.bucketName = config.getBlobServiceBucketName();

        LOGGER.info("Service account: " + this.appIdentityService.getServiceAccountName());
    }


    @Override
    public void put(AuthenticatedUser user,
                    BlobId blobId,
                    String contentDisposition,
                    String mimeType,
                    ByteSource byteSource) throws IOException {

        writeMetadata(user, blobId, contentDisposition);
        getBlobResource(blobId).put(contentDisposition, mimeType, byteSource);
    }


    @Override
    public UploadCredentials startUpload(
        @InjectParam AuthenticatedUser user,
        @FormParam("blobId") BlobId blobId,
        @FormParam("filename") String filename) {

        if (user == null || user.isAnonymous()) {
            throw new WebApplicationException(UNAUTHORIZED);
        }

        if (blobId == null || Strings.isNullOrEmpty(filename)) {
            throw new BadRequestException("The blobId and filename parameters are required as form parameters.");
        }

        writeMetadata(user, blobId, filename);

        GcsUploadCredentialBuilder builder = new GcsUploadCredentialBuilder(appIdentityService);
        builder.setBucket(bucketName);
        builder.setKey(blobId.asString());
        builder.setMaxContentLengthInMegabytes(10);
        builder.expireAfter(Period.minutes(5));
        return builder.build();
    }

    private void writeMetadata(AuthenticatedUser user, BlobId blobId, String filename) {
        Transaction tx = datastore.beginTransaction();

        Optional<Entity> existing = getMetadata(tx, blobId);
        if(existing.isPresent()) {
            // if the user has previously tried to start an upload for this blob,
            // and the filename has changed, then we can provide credentials again
            // but otherwise signal a conflict

            long userId = (Long)existing.get().getProperty("user");
            String existingFilename = (String)existing.get().getProperty("filename");
            if(userId != user.getUserId() || existingFilename.equals(filename)) {
                throw new WebApplicationException(Response
                    .status(Response.Status.CONFLICT)
                    .entity("The blobId already exists.").build());
            }

        } else {
            // otherwise create a new entity linking this blob to the user who is uploading it.
            Entity metadata = new Entity(KIND, blobId.asString());
            metadata.setProperty("user", user.getId());
            metadata.setUnindexedProperty("filename", filename);
            // index the size of zero so the we can fetch the actual size later from the data
            metadata.setProperty("size", 0L);
            datastore.put(tx, metadata);
            tx.commit();
        }
    }

    private Optional<Entity> getMetadata(Transaction tx, BlobId blobId) {
        try {
            return Optional.of(datastore.get(tx, metadataKey(blobId)));
        } catch (EntityNotFoundException e) {
            return Optional.absent();
        }
    }

    private Key metadataKey(BlobId blobId) {
        return KeyFactory.createKey(KIND, blobId.asString());
    }


    @Override
    public BlobResource getBlob(@InjectParam AuthenticatedUser user, @PathParam("blobId") BlobId blobId) {
        Optional<Entity> metadata = getMetadata(null, blobId);
        if(!metadata.isPresent()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        long ownerId = (Long)metadata.get().getProperty("user");
        if(ownerId != user.getId()) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        return getBlobResource(blobId);
    }

    private GcsBlobResource getBlobResource(BlobId blobId) {
        return new GcsBlobResource(bucketName, blobId);
    }
}
