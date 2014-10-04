package org.activityinfo.service.blob;

import com.sun.jersey.core.header.ContentDisposition;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MediaType;

public class BlobMetadata {
    private static final String ATTACHMENT = "attachment";

    private final BlobId blobId;
    private final ContentDisposition contentDisposition;
    private final MediaType contentType;

    public BlobMetadata(BlobId blobId, ContentDisposition contentDisposition, MediaType contentType) {
        this.blobId = blobId;
        this.contentDisposition = contentDisposition;
        this.contentType = contentType;
    }

    /**
     * Creates a {@code BlobMetadata} instance for an attachment.
     * @param filename the blob's filename
     * @param contentType the blob's contentType
     * @return a new {@code BlobMetadata} instance
     */
    public static BlobMetadata attachment(@Nonnull BlobId blobId, @Nonnull String filename, @Nonnull MediaType contentType) {
        return new BlobMetadata(blobId, ContentDisposition.type(ATTACHMENT).fileName(filename).build(), contentType);
    }

    public static BlobMetadata attachment(@Nonnull BlobId blobId, @Nonnull String fileName) {
        return attachment(blobId, fileName, MediaType.APPLICATION_OCTET_STREAM_TYPE);
    }


    public BlobId getBlobId() {
        return blobId;
    }

    public ContentDisposition getContentDisposition() {
        return contentDisposition;
    }

    public MediaType getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return "BlobMetadata{" +
            "blobId=" + blobId +
            ", contentDisposition=" + contentDisposition +
            ", contentType=" + contentType +
            '}';
    }
}
