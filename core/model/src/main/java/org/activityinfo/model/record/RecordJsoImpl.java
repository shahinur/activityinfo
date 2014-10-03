package org.activityinfo.model.record;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.json.client.*;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.RecordFieldType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class RecordJsoImpl implements Record {

    private final JSONObject record;

    public RecordJsoImpl(JSONObject record) {
        this.record = record;
    }

    @Override
    public ResourceId getClassId() {
        return ResourceId.valueOf(record.get("@class").isString().stringValue());
    }

    @Override
    public Object get(String fieldName) {
        return parseValue(record.get(fieldName));
    }

    private Object parseValue(JSONValue value) {
        if(value instanceof JSONNumber) {
            return ((JSONNumber) value).doubleValue();
        } else if(value instanceof JSONString) {
            return value.isString().stringValue();
        } else if(value instanceof JSONObject) {
            return new RecordJsoImpl((JSONObject) value);
        } else if(value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            List<Object> elements = Lists.newArrayList();
            for(int i=0;i!=array.size();++i) {
                elements.add(parseValue(array.get(i)));
            }
            return elements;
        } else if(value instanceof JSONNull || value == null) {
            return null;
        } else {
            throw new IllegalArgumentException("value: " + value);
        }
    }

    @Override
    public boolean has(String fieldName) {
        return record.get(fieldName) != null;
    }

    @Override
    public boolean getBoolean(String fieldName) {
        JSONValue value = record.get(fieldName);
        return ((JSONBoolean) value).booleanValue();
    }

    @Override
    public Boolean isBoolean(String fieldName) {
        JSONValue value = record.get(fieldName);
        if(value instanceof JSONBoolean) {
            return ((JSONBoolean) value).booleanValue();
        } else {
            return null;
        }
    }

    @Override
    public boolean getBoolean(String fieldName, boolean defaultValue) {
        JSONValue value = record.get(fieldName);
        if(value instanceof JSONBoolean) {
            return ((JSONBoolean) value).booleanValue();
        } else {
            return defaultValue;
        }
    }

    @Nonnull
    @Override
    public List<Record> getRecordList(String fieldName) {
        JSONValue value = record.get(fieldName);
        if(value instanceof JSONArray) {
            List<Record> records = Lists.newArrayList();
            JSONArray array = (JSONArray) value;
            for(int i=0;i!=array.size();++i) {
                records.add(new RecordJsoImpl((JSONObject) array.get(i)));
            }
            return records;
        } else {
            return Collections.emptyList();
        }
    }

    @Nonnull
    @Override
    public List<String> getStringList(String fieldName) {
        JSONValue value = record.get(fieldName);
        if(value instanceof JSONArray) {
            List<String> strings = Lists.newArrayList();
            JSONArray array = (JSONArray) value;
            for(int i=0;i!=array.size();++i) {
                strings.add(array.get(i).isString().stringValue());
            }
            return strings;
        } else {
            return Collections.emptyList();
        }
    }

    @Nonnull
    @Override
    public String getString(String fieldName) {
        return record.get(fieldName).isString().stringValue();
    }

    @Override
    public String isString(String fieldName) {
        JSONValue jsonValue = record.get(fieldName);
        if(jsonValue != null) {
            JSONString value = jsonValue.isString();
            if(value != null ) {
                return value.stringValue();
            }
        }
        return null;
    }

    public String getString(String fieldName, String defaultValue) {
        String value = has(fieldName) ? isString(fieldName) : null;
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    @Nonnull
    @Override
    public Record getRecord(String fieldName) {
        JSONObject value = record.get(fieldName).isObject();
        return new RecordJsoImpl(value);
    }

    @Override
    public Record isRecord(String fieldName) {
        JSONValue jsonValue = record.get(fieldName);
        if(jsonValue != null) {
            JSONObject jsonObject = jsonValue.isObject();
            if (jsonObject != null) {
                return new RecordJsoImpl(jsonObject);
            }
        }
        return null;
    }

    @Override
    public double getDouble(String fieldName) {
        return record.get(fieldName).isNumber().doubleValue();
    }

    @Override
    public int getInt(String fieldName) {
        return (int)getDouble(fieldName);
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = Maps.newHashMap();
        for(String fieldName : record.keySet()) {
            if(fieldName.charAt(0) != '@') {
                map.put(fieldName, parseValue(record.get(fieldName)));
            }
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return RecordFieldType.TYPE_CLASS;
    }

    @Override
    public Record asRecord() {
        return this;
    }
}
