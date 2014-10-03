package org.activityinfo.service.tasks;

import org.activityinfo.model.annotation.RecordBean;
import org.activityinfo.service.blob.BlobId;

@RecordBean(classId = "_blobResult")
public class BlobResult {

    private String filename;
    private String blobId;

    public BlobResult() {
    }

    public BlobResult(BlobId blobId, String filename) {
        this.filename = filename;
        this.blobId = blobId.asString();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getBlobId() {
        return blobId;
    }

    public void setBlobId(String blobId) {
        this.blobId = blobId;
    }
}
