package org.activityinfo.service.tasks;

import org.activityinfo.model.annotation.RecordBean;
import org.activityinfo.model.annotation.Reference;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.blob.BlobId;

@RecordBean(classId = "_exportFormTask")
public class ExportFormTaskModel implements TaskModel {

    private ResourceId formClassId;
    private String blobId;
    private String filename;

    @Reference(range = FormClass.class)
    public ResourceId getFormClassId() {
        return formClassId;
    }

    public void setFormClassId(ResourceId formClassId) {
        this.formClassId = formClassId;
    }

    public String getBlobId() {
        return blobId;
    }

    public void setBlobId(String blobId) {
        this.blobId = blobId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setBlobId(BlobId blobId) {
        this.blobId = blobId.asString();
    }
}
