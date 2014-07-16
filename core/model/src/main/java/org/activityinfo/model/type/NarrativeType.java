package org.activityinfo.model.type;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.component.ComponentReader;

/**
 * FieldType containing paragraph-like text.
 *
 */
public enum NarrativeType implements FieldType, FieldTypeClass {

    INSTANCE;




    @Override
    public FieldTypeClass getTypeClass() {
        return this;
    }

    @Override
    public Record getParameters() {
        return null;
    }

    @Override
    public ComponentReader<String> getStringReader(String fieldName, String componentId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(String name, String componentId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getId() {
        return "NARRATIVE";
    }

    @Override
    public String getLabel() {
        return "Multi-line Text";
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
        return null;
    }

}
