package org.activityinfo.model.resource;

import com.google.common.collect.Lists;

import java.util.List;

public class Records {

    public static List<Record> toRecordList(Iterable<? extends IsRecord> objects) {
        List<Record> records = Lists.newArrayList();
        for(IsRecord object : objects) {
            records.add(object.asRecord());
        }
        return records;
    }
}
