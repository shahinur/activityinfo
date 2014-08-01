package org.activityinfo.model.type;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceIdPrefixType;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;

public enum TextType implements FieldType, FieldTypeClass {

    INSTANCE;



    @Override
    public String getId() {
        return "FREE_TEXT";
    }

    @Override
    public String getLabel() {
        return "Text";
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return this;
    }

    @Override
    public Record getParameters() {
        return new Record().set("classId", getTypeClass().getParameterFormClass().getId());
    }

    @Override
    public ComponentReader getStringReader(final String fieldName, String componentId) {
        assert DEFAULT_COMPONENT.equals(componentId);

        return new ComponentReader() {

            @Override
            public String read(Resource resource) {
                return resource.isString(fieldName);
            }
        };
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(String name, String componentId) {
        return new NullComponentReader<>();
    }


    @Override
    public FieldType createType(Record typeParameters) {
        return this;
    }

    @Override
    public FieldType createType() {
        return this;
    }


    @Override
    public FormClass getParameterFormClass() {
        return new FormClass(ResourceIdPrefixType.TYPE.id("text"));
    }
}
