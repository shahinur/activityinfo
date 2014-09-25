package org.activityinfo.model.system;

import org.activityinfo.model.resource.*;

public class FolderInstance implements IsResource {

    private ResourceId id;
    private ResourceId ownerId;
    private String label;
    private String description;

    @Override
    public ResourceId getId() {
        return id;
    }

    public void setId(ResourceId id) {
        this.id = id;
    }

    public ResourceId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ResourceId ownerId) {
        this.ownerId = ownerId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Resource asResource() {
        RecordBuilder record = Records.builder(FolderClass.CLASS_ID)
            .set(FolderClass.LABEL_FIELD_ID.asString(), label)
            .set(FolderClass.DESCRIPTION_FIELD_ID.asString(), description);


        return Resources.createResource()
            .setId(id)
            .setOwnerId(ownerId)
            .setValue(record.build());

    }
}
