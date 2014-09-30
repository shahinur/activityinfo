package org.activityinfo.model.record;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceIdPrefixType;
import org.activityinfo.model.type.FieldTypeClass;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

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

    public static boolean deepEquals(@NotNull Record x, @NotNull Record y) {
        ResourceId xClassId = x.getClassId(), yClassId = y.getClassId();
        Map<String, Object> xMap = x.asMap(), yMap = y.asMap();

        if (xClassId != null ? !xClassId.equals(yClassId) : yClassId != null) return false;
        if (xMap != null ? yMap == null || !mapEquals(xMap, yMap) : yMap != null) return false;

        return true;
    }

    private static boolean mapEquals(@NotNull Map<String, Object> x, @NotNull Map<String, Object> y) {
        if(x.size() != y.size()) {
            return false;
        }

        for(String fieldName : x.keySet()) {
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
                if(!fy.equals(fx)) {
                    return false;
                }
            }
        }
        return true;
    }
}
