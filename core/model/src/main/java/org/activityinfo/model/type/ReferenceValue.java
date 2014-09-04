package org.activityinfo.model.type;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;

import java.util.List;
import java.util.Set;

/**
 * A Field Value containing the value of {@code ReferenceType} or
 * {@code EnumType}
 */
public class ReferenceValue implements FieldValue, IsRecord, HasSetFieldValue {


    private final Set<ResourceId> resourceIds;

    public ReferenceValue(ResourceId resourceId) {
        assert resourceId != null;
        this.resourceIds = ImmutableSet.of(resourceId);
    }

    public ReferenceValue(ResourceId... resourceIds) {
        assert resourceIds.length > 0;
        this.resourceIds = ImmutableSet.copyOf(resourceIds);
    }

    public ReferenceValue(Iterable<ResourceId> resourceIds) {
        assert !Iterables.isEmpty(resourceIds);
        this.resourceIds = ImmutableSet.copyOf(resourceIds);
    }

    public Set<ResourceId> getResourceIds() {
        return resourceIds;
    }

    public ResourceId getResourceId() {
        Preconditions.checkState(resourceIds.size() == 1);
        return resourceIds.iterator().next();
    }

    @Override
    public Record asRecord() {
        Record record = new Record();
        record.set(TYPE_CLASS_FIELD_NAME, ReferenceType.TYPE_CLASS.getId());

        if(resourceIds.size() == 1) {
            record.set("value", resourceIds.iterator().next().asString());
        } else if(resourceIds.size() > 1) {
            record.set("value", toStringList(resourceIds));
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

    public static ReferenceValue fromRecord(Record record) {
        String id = record.isString("value");
        if(id != null) {
            return new ReferenceValue(ResourceId.valueOf(id));
        }
        List<String> strings = record.getStringList("value");
        if(strings.size() > 0) {
            Set<ResourceId> ids = Sets.newHashSet();
            for (String string : strings) {
                ids.add(ResourceId.valueOf(string));
            }
            return new ReferenceValue(ids);
        }
        return null;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return ReferenceType.TYPE_CLASS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReferenceValue that = (ReferenceValue) o;

        if (!resourceIds.equals(that.resourceIds)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return resourceIds.hashCode();
    }

    @Override
    public String toString() {
        return "ReferenceValue[" + Joiner.on(", ").join(resourceIds) + "]";
    }
}
