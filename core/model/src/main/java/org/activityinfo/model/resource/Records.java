package org.activityinfo.model.resource;

import com.google.common.collect.Lists;
import org.activityinfo.model.type.FieldTypeClass;

import java.util.List;

public class Records {

    public static List<Record> toRecordList(Iterable<? extends IsRecord> objects) {
        List<Record> records = Lists.newArrayList();
        for(IsRecord object : objects) {
            records.add(object.asRecord());
        }
        return records;
    }

    public static RecordBuilder builder() {
        return builder((ResourceId)null);
    }

    public static RecordBuilder builder(ResourceId formClassId) {
        return new RecordBuilderImpl(formClassId);
    }

    public static RecordBuilder builder(FieldTypeClass typeClass) {
        return new RecordBuilderImpl(ResourceIdPrefixType.TYPE.id(typeClass.getId()));
    }

    public static RecordBuilder buildCopyOf(Record record) {
        return new RecordBuilderImpl(record);
    }

    public static boolean deepEquals(Record x, Record y) {

        if(x.asMap().size() != y.asMap().size()) {
            return false;
        }

        for(String fieldName : x.asMap().keySet()) {
            Object fx = x.get(fieldName);
            Object fy = y.get(fieldName);

            if(fy == null) {
                return false;
            }
            if(fx instanceof Record && fy instanceof Record) {
                if(!deepEquals((Record)fx, (Record)fy)) {
                    return false;
                }
            } else {
                if(!fx.equals(fy)) {
                    return false;
                }
            }
        }
        return true;
    }
}
