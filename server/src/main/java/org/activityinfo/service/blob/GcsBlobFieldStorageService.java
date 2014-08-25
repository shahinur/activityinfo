package org.activityinfo.service.blob;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFileOptions.Builder;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.common.io.ByteSource;
import com.google.inject.Inject;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.server.DeploymentEnvironment;
import org.activityinfo.server.util.blob.DevAppIdentityService;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.service.gcs.GcsAppIdentityServiceUrlSigner;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.util.logging.Logger;

public class GcsBlobFieldStorageService implements BlobFieldStorageService {

    private static final Logger LOGGER = Logger.getLogger(GcsBlobFieldStorageService.class.getName());

    private final String bucketName;
    private AppIdentityService appIdentityService;

    @Inject
    public GcsBlobFieldStorageService(DeploymentConfiguration config) {
        this.bucketName = config.getBlobServiceBucketName();
        appIdentityService = DeploymentEnvironment.isAppEngineDevelopment() ?
                new DevAppIdentityService(config) : AppIdentityServiceFactory.getAppIdentityService();

        LOGGER.info("Service account: " + appIdentityService.getServiceAccountName());
    }

    @Override
    public UploadCredentials getUploadCredentials(ResourceId userId, BlobId blobId) {
        GcsUploadCredentialBuilder builder = new GcsUploadCredentialBuilder(appIdentityService);
        builder.setBucket(bucketName);
        builder.setKey(blobId.asString());
        builder.setMaxContentLengthInMegabytes(5);
        return builder.build();
    }

    @Override
    public URI getBlobUrl(BlobId blobId) {
        GcsAppIdentityServiceUrlSigner signer = new GcsAppIdentityServiceUrlSigner();
        try {
            return new URI(signer.getSignedUrl("GET", bucketName + "/" + blobId.asString()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void put(AuthenticatedUser authenticatedUser, String contentDisposition, String mimeType, BlobId blobId,
                    ByteSource byteSource) throws IOException {
        GcsFilename gcsFilename = new GcsFilename(bucketName, blobId.asString());
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        Builder builder = new Builder();

        builder.contentDisposition(contentDisposition);
        builder.mimeType(mimeType);

        GcsFileOptions gcsFileOptions = builder.build();
        GcsOutputChannel channel = gcsService.createOrReplace(gcsFilename, gcsFileOptions);

        try (OutputStream outputStream = Channels.newOutputStream(channel)) {
            byteSource.copyTo(outputStream);
        }
    }

    @Override
    public Response getThumbnail(@InjectParam AuthenticatedUser user,
                                 @PathParam("resourceId") ResourceId resourceId,
                                 @PathParam("fieldName") String fieldName,
                                 @PathParam("blobId") BlobId blobId,
                                 @QueryParam("width") int width,
                                 @QueryParam("height") int height) {
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

        BlobKey blobKey = blobstoreService.createGsBlobKey("/gs/" + bucketName + "/" + blobId.asString());

        Image image = ImagesServiceFactory.makeImageFromBlob(blobKey);
        byte[] imageData;

        // TODO Do not perform a resize transform if the requested width and height match those from the resource store
//        if (width != image.getWidth() || image.getHeight() != height) {
            Transform resize = ImagesServiceFactory.makeResize(width, height);
            Image newImage = imagesService.applyTransform(resize, image);

            imageData = newImage.getImageData();
//        }

        return Response.ok(imageData).build();
    }
}
