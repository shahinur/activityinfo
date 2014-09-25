package org.activityinfo.model.resource;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.RecordFieldType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class RecordImpl implements Record {

    private ResourceId classId;
    private ImmutableMap<String, Object> map;

    RecordImpl(ResourceId classId, ImmutableMap<String, Object> map) {
        this.classId = classId;
        this.map = map;
    }

    public ResourceId getClassId() {
        return classId;
    }

    public void setClassId(ResourceId classId) {
        this.classId = classId;
    }

    @Override
    public Object get(String fieldName) {
        return map.get(fieldName);
    }

    @Override
    public boolean has(String fieldName) {
        return map.containsKey(fieldName);
    }

    @Override
    public boolean getBoolean(String fieldName) {
        assert map.containsKey(fieldName) : fieldName + " has no value";
        assert map.get(fieldName) instanceof Boolean :
            "Expected boolean type for field " + fieldName + ", found: " + map.get(fieldName).getClass().getName();

        return (Boolean)map.get(fieldName);
    }

    @Override
    public Boolean isBoolean(String fieldName) {
        Object value = map.get(fieldName);
        if(value instanceof Boolean) {
            return (Boolean)value;
        }
        return null;
    }

    @Override
    public boolean getBoolean(String fieldName, boolean defaultValue) {
        Object value = map.get(fieldName);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return defaultValue;
        }
    }

    @Nonnull
    @Override
    public List<Record> getRecordList(String fieldName) {
        Object value = map.get(fieldName);
        if(value instanceof List) {
            return (List<Record>) value;
        }
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<String> getStringList(String fieldName) {
        Object value = map.get(fieldName);
        if(value instanceof List) {
            return (List<String>) value;
        }
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public String getString(String fieldName) {
        assert !fieldName.contentEquals("classId");
        assert map.containsKey(fieldName) : fieldName + " has no value";
        assert map.get(fieldName) instanceof String :
            "Expected String type for field " + fieldName + ", found: " + map.get(fieldName).getClass().getName();

        return (String)map.get(fieldName);
    }

    @Override
    public String isString(String fieldName) {
        assert !fieldName.contentEquals("classId");

        Object value = map.get(fieldName);
        if(value instanceof String) {
            return (String)value;
        }
        return null;
    }

    @Nonnull
    @Override
    public Record getRecord(String fieldName) {
        assert map.containsKey(fieldName) : fieldName + " has no value";
        assert map.get(fieldName) instanceof Record :
            "Expected Record type for field " + fieldName + ", found: " + map.get(fieldName).getClass().getName();

        return (Record)map.get(fieldName);
    }

    @Override
    public Record isRecord(String fieldName) {
        Object value = map.get(fieldName);
        if(value instanceof Record) {
            return (Record)value;
        }
        return null;
    }

    @Override
    public double getDouble(String fieldName) {
        assert map.containsKey(fieldName) : fieldName + " has no value";
        assert map.get(fieldName) instanceof Double :
            "Expected double type for field " + fieldName + ", found: " + map.get(fieldName).getClass().getName();

        return (Double)map.get(fieldName);
    }

    @Override
    public int getInt(String fieldName) {
        return (int)getDouble(fieldName);
    }

    @Override
    public Map<String, Object> asMap() {
        return map;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return RecordFieldType.TYPE_CLASS;
    }

    @Override
    public String toString() {
        return "{" + classId + ": " + Joiner.on(",").withKeyValueSeparator("=").join(map) + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecordImpl)) return false;

        RecordImpl record = (RecordImpl) o;

        if (classId != null ? !classId.equals(record.classId) : record.classId != null) return false;
        if (map != null ? !map.equals(record.map) : record.map != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = classId != null ? classId.hashCode() : 0;
        result = 31 * result + (map != null ? map.hashCode() : 0);
        return result;
    }
}
