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
    public Record asRecord() {
        return FolderClass.INSTANCE.toRecord(this);
    }
}
