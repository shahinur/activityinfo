package org.activityinfo.model.type.enumerated;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EnumFieldValue implements FieldValue, IsRecord {

    public static final EnumFieldValue EMPTY = new EnumFieldValue(Collections.<ResourceId>emptySet());

    private final Set<ResourceId> valueIds;

    public EnumFieldValue(ResourceId valueId) {
        this.valueIds = ImmutableSet.of(valueId);
    }

    public EnumFieldValue(ResourceId... valueIds) {
        this.valueIds = ImmutableSet.copyOf(valueIds);
    }

    public EnumFieldValue(Iterable<ResourceId> valueIds) {
        this.valueIds = ImmutableSet.copyOf(valueIds);
    }

    public Set<ResourceId> getValueIds() {
        return valueIds;
    }

    public ResourceId getValueId() {
        Preconditions.checkState(valueIds.size() == 1);
        return valueIds.iterator().next();
    }

    @Override
    public Record asRecord() {
        Record record = new Record();
        record.set(TYPE_CLASS_FIELD_NAME, EnumType.TYPE_CLASS.getId());

        if(valueIds.size() == 1) {
            record.set("value", valueIds.iterator().next().asString());
        } else if(valueIds.size() > 1) {
            record.set("value", toStringList(valueIds));
        }
        return record;
    }

    private List<String> toStringList(Set<ResourceId> resourceIds) {
        List<String> strings = Lists.newArrayList();
        for(ResourceId resourceId : resourceIds) {
            strings.add(resourceId.asString());
        }
        return strings;
    }

    public static EnumFieldValue fromRecord(Record record) {
        String id = record.isString("value");
        if(id != null) {
            return new EnumFieldValue(ResourceId.create(id));
        }
        List<String> strings = record.getStringList("value");
        Set<ResourceId> ids = Sets.newHashSet();
        for(String string : strings) {
            ids.add(ResourceId.create(string));
        }
        return new EnumFieldValue(ids);
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return EnumType.TYPE_CLASS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EnumFieldValue that = (EnumFieldValue) o;

        if (!valueIds.equals(that.valueIds)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return valueIds.hashCode();
    }

    @Override
    public String toString() {
        return "EnumFieldValue[" + Joiner.on(", ").join(valueIds) + "]";
    }
}
