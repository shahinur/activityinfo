package org.activityinfo.store.blob;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.tools.cloudstorage.*;
import com.google.common.io.ByteSource;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobResource;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

public class GcsBlobResource implements BlobResource {

    public static final int PREFETCH_SIZE = 2 << 18;
    public static final int BEGINNING = 0;

    private String bucketName;
    private BlobId blobId;

    public GcsBlobResource(String bucketName, BlobId blobId) {
        this.bucketName = bucketName;
        this.blobId = blobId;
    }


    @Override
    public Response get() {

        GcsAppIdentityServiceUrlSigner signer = new GcsAppIdentityServiceUrlSigner();
        return Response.temporaryRedirect(signer.getSignedUrl("GET", bucketName + "/" + blobId.asString())).build();
    }


    public GcsFilename getFileName(BlobId blobId) {
        return new GcsFilename(bucketName, blobId.asString());
    }

    @Override
    public Response getThumbnail(@QueryParam("width") int width,
                                 @QueryParam("height") int height) {

        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

        BlobKey blobKey = blobstoreService.createGsBlobKey("/gs/" + bucketName + "/" + blobId.asString());

        Image image = ImagesServiceFactory.makeImageFromBlob(blobKey);

        Transform resize = ImagesServiceFactory.makeResize(width, height);
        Image newImage = imagesService.applyTransform(resize, image);

        byte[] imageData = newImage.getImageData();

        return Response.ok(imageData).build();
    }

    @Override
    public ByteSource getContent() {
        return new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                GcsFilename gcsFilename = getFileName(blobId);
                GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
                GcsInputChannel channel = gcsService.openPrefetchingReadChannel(gcsFilename, BEGINNING, PREFETCH_SIZE);
                return Channels.newInputStream(channel);
            }
        };
    }

    void put(String contentDisposition, String contentType, ByteSource byteSource) throws IOException {
        try (OutputStream outputStream = openOutputStream(contentDisposition, contentType)) {
            byteSource.copyTo(outputStream);
        }
    }

    OutputStream openOutputStream(String contentDisposition, String contentType) throws IOException {
        GcsFilename gcsFilename = getFileName(blobId);
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        GcsFileOptions.Builder builder = new GcsFileOptions.Builder();
        builder.contentDisposition(contentDisposition);
        builder.mimeType(contentType);

        GcsFileOptions gcsFileOptions = builder.build();
        GcsOutputChannel channel = gcsService.createOrReplace(gcsFilename, gcsFileOptions);
        return Channels.newOutputStream(channel);
    }
}
