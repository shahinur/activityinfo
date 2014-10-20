package org.activityinfo.model.type.time;

import org.activityinfo.model.annotation.ValueOf;
import org.activityinfo.model.record.IsRecord;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;

@ValueOf(InstantType.class)
public class Instant implements IsRecord, FieldValue {

    private long time;

    public Instant(double time) {
        this.time = (long) time;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return InstantType.TYPE_CLASS;
    }

    @Override
    public Record asRecord() {
        return Records.builder()
                .set(TYPE_CLASS_FIELD_NAME, getTypeClass().getId())
                .set("time", time)
                .build();
    }

    public static Instant fromRecord(Record record) {
        return new Instant(record.getDouble("time"));
    }
}
