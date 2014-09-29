package org.activityinfo.model.record;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.activityinfo.model.type.primitive.TextValue;

import java.util.HashMap;
import java.util.Map;

class RecordBuilderImpl implements RecordBuilder {

    private ResourceId classId;
    private Map<String, Object> bag = new HashMap<>();

    RecordBuilderImpl(ResourceId classId) {
        this.classId = classId;
    }

    public RecordBuilderImpl(Record record) {
        this.classId = record.getClassId();
        this.bag.putAll(record.asMap());
    }

    @Override
    public RecordBuilder setClassId(ResourceId classId) {
        assert classId != null;
        this.classId = classId;
        return this;
    }

    @Override
    public RecordBuilder setClassId(String classId) {
        return setClassId(ResourceId.valueOf(classId));
    }

    @Override
    public RecordBuilder set(String fieldName, String value) {
        if(value == null) {
            bag.remove(fieldName);
        } else {
            if(fieldName.equals("classId")) {
                this.classId = ResourceId.valueOf(value);
            } else {
                bag.put(fieldName, value);
            }
        }
        return this;
    }

    @Override
    public RecordBuilder set(String fieldName, double value) {
        bag.put(fieldName, value);
        return this;
    }

    @Override
    public RecordBuilder set(String fieldName, boolean value) {
        bag.put(fieldName, value);
        return this;
    }

    @Override
    public RecordBuilder set(String fieldName, Record record) {
        if(record == null) {
            bag.remove(fieldName);
        } else {
            bag.put(fieldName, record);
        }
        return this;
    }

    @Override
    public RecordBuilder set(String fieldName, IsRecord record) {
        bag.put(fieldName, record.asRecord());
        return this;
    }

    @Override
    public RecordBuilder setFieldValue(String fieldName, FieldValue value) {
        setFieldValue(this, fieldName, value);
        return this;
    }

    static void setFieldValue(RecordBuilder builder, String fieldName, FieldValue value) {
        if(value == null) {
            builder.set(fieldName, (String)null);

        } else if(value instanceof TextValue) {
            builder.set(fieldName, ((TextValue) value).asString());

        } else if(value instanceof BooleanFieldValue) {
            builder.set(fieldName, value == BooleanFieldValue.TRUE);

        } else if(value instanceof IsRecord) {
            builder.set(fieldName, ((IsRecord) value).asRecord());

        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public RecordBuilder set(String fieldName, Iterable<?> value) {
        bag.put(fieldName, ImmutableList.copyOf(value));
        return this;
    }

    @Override
    public RecordBuilder set(String fieldName, ResourceId value) {
        bag.put(fieldName, new ReferenceValue(value).asRecord());
        return this;
    }

    @Override
    public RecordBuilder set(String fieldName, Enum<?> enumValue) {
        bag.put(fieldName, enumValue.name());
        return this;
    }

    @Override
    public Record build() {
        return new RecordImpl(classId, ImmutableMap.copyOf(bag));
    }
}
