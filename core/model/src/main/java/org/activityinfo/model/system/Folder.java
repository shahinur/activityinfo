package org.activityinfo.model.system;

import org.activityinfo.model.annotation.RecordBean;
import org.activityinfo.model.record.IsRecord;
import org.activityinfo.model.record.Record;

@RecordBean(classId = "_folder")
public class Folder implements IsRecord {

    private String label;
    private String description;

    public String getLabel() {
        return label;
    }

    public Folder setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Folder setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public Record asRecord() {
        return FolderClass.INSTANCE.toRecord(this);
    }
}
