package org.activityinfo.model.table;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;

public class ResourceIdSource extends ColumnSource {

    public static final String SOURCE_TYPE = "id";


    @Override
    public Record asRecord() {
        RecordBuilder record = Records.builder();
        record.set("type", SOURCE_TYPE);
        return record.build();
    }
}
