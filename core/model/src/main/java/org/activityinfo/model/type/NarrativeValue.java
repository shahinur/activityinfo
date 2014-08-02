package org.activityinfo.model.type;

import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;

public class NarrativeValue implements FieldValue, IsRecord {

    private String text;

    public NarrativeValue(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return NarrativeType.TYPE_CLASS;
    }

    @Override
    public Record toRecord() {
        return new Record()
                .set(TYPE_CLASS_FIELD_NAME, getTypeClass().getId())
                .set("text", text);
    }

    public static FieldValue fromRecord(Record record) {
        return new NarrativeValue(record.getString("text"));
    }
}
