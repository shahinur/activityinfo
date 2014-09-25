package org.activityinfo.model.resource;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.enumerated.EnumFieldValue;

import javax.annotation.Nonnull;

final class RecordBuilderJsoImpl extends JavaScriptObject implements RecordBuilder {


    protected RecordBuilderJsoImpl() {
    }

    static native RecordBuilderJsoImpl create() /*-{
        return {};
    }-*/;

    @Override
    public RecordBuilder setClassId(ResourceId classId) {
        return setClassId(classId.asString());
    }

    @Override
    public RecordBuilder setClassId(String classId) {
        set("@class", classId);
        return this;
    }

    @Override
    public native RecordBuilder set(String fieldName, @Nonnull String value) /*-{
        if(value != null) {
            this[fieldName] = value;
        }
        return this;
    }-*/;

    @Override
    public native RecordBuilder set(String fieldName, double value) /*-{
        this[fieldName] = value;
        return this;
    }-*/;

    @Override
    public native RecordBuilder set(String fieldName, boolean value) /*-{
        this[fieldName] = value;
        return this;
    }-*/;

    @Override
    public RecordBuilder set(String fieldName, FieldValue value) {
        RecordBuilderImpl.setFieldValue(this, fieldName, value);
        return this;
    }

    @Override
    public RecordBuilder set(String fieldName, Iterable<?> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RecordBuilder set(String fieldName, ResourceId value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RecordBuilder set(String fieldName, Enum<?> enumValue) {
        return set(fieldName, new EnumFieldValue(ResourceId.valueOf(enumValue.name())).asRecord());
    }

    @Override
    public RecordBuilder set(String fieldName, Record record) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RecordBuilder set(String fieldName, IsRecord record) {
        return set(fieldName, record.asRecord());
    }

    @Override
    public Record build() {
        return new RecordJsoImpl(new JSONObject(this));
    }
}
