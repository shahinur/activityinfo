package org.activityinfo.store.blob;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.*;
import com.google.common.io.ByteSource;
import org.activityinfo.service.blob.UploadCredentials;
import org.joda.time.Period;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

class UserBlobStorageObject {

    public static final int PREFETCH_SIZE = 2 << 18;
    public static final int BEGINNING = 0;

    private final UserBlob blob;

    private final String bucketName;
    private final String key;

    public UserBlobStorageObject(String bucketName, UserBlob blob) {
        this.bucketName = bucketName;
        this.blob = blob;
        this.key = blob.getBlobId().asString();
    }

    public GcsFilename getFileName() {
        return new GcsFilename(bucketName, key);
    }

    public java.net.URI getSignedUrl() {
        GcsAppIdentityServiceUrlSigner signer = new GcsAppIdentityServiceUrlSigner();
        return signer.getSignedUrl("GET", bucketName + "/" + key);
    }

    public BlobKey getBlobstoreKey() {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        return blobstoreService.createGsBlobKey("/gs/" + bucketName + "/" + key);
    }

    public ByteSource asByteSource() {
        return new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                GcsFilename gcsFilename = getFileName();
                GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
                GcsInputChannel channel = gcsService.openPrefetchingReadChannel(gcsFilename, BEGINNING, PREFETCH_SIZE);
                return Channels.newInputStream(channel);
            }
        };
    }

    public OutputStream openOutputStream() throws IOException {
        GcsFilename gcsFilename = getFileName();
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        GcsFileOptions.Builder builder = new GcsFileOptions.Builder();
        builder.contentDisposition(blob.getMetadata().getContentDisposition().toString());
        builder.mimeType(blob.getMetadata().getContentType().toString());

        GcsFileOptions gcsFileOptions = builder.build();
        GcsOutputChannel channel = gcsService.createOrReplace(gcsFilename, gcsFileOptions);
        return Channels.newOutputStream(channel);
    }

    public UploadCredentials getUploadCredentials() {
        GcsUploadCredentialBuilder builder = new GcsUploadCredentialBuilder();
        builder.setBucket(bucketName);
        builder.setKey(key);
        builder.setMaxContentLengthInMegabytes(10);
        builder.expireAfter(Period.minutes(5));
        return builder.build();
    }

}
