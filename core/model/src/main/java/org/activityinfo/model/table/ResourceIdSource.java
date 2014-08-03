package org.activityinfo.model.table;

import org.activityinfo.model.resource.Record;

public class ResourceIdSource extends ColumnSource {

    public static final String SOURCE_TYPE = "id";


    @Override
    public Record asRecord() {
        Record record = new Record();
        record.set("type", SOURCE_TYPE);
        return record;
    }
}
