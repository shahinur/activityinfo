package org.activityinfo.ui.app.client.request;

import org.activityinfo.service.blob.BlobId;

public class UploadResult {

    private final BlobId blobId;

    public UploadResult(BlobId blobId) {
        this.blobId = blobId;
    }

    public BlobId getBlobId() {
        return blobId;
    }
}
