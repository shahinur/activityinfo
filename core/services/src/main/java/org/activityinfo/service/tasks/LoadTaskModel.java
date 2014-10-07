package org.activityinfo.service.tasks;

import org.activityinfo.model.annotation.RecordBean;
import org.activityinfo.model.annotation.Reference;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.Folder;

/**
 * Defines a task to import a user's blob as a {@code FormClass}
 * and corresponding resources.
 */
@RecordBean(classId = "_loadTask")
public class LoadTaskModel implements TaskModel {

    private String blobId;

    private ResourceId folderId;

    /**
     *
     * @return the source to load
     */
    public String getBlobId() {
        return blobId;
    }

    public void setBlobId(String blobId) {
        this.blobId = blobId;
    }

    /**
     *
     * @return the folder in which the new form is to be created
     */
    @Reference(range = Folder.class)
    public ResourceId getFolderId() {
        return folderId;
    }

    public void setFolderId(ResourceId folderId) {
        this.folderId = folderId;
    }

    @Override
    public Record asRecord() {
        return LoadTaskModelClass.INSTANCE.toRecord(this);
    }
}
