package org.activityinfo.model.type.enumerated;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.collect.Lists;
import org.activityinfo.model.form.FieldId;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.component.ComponentReader;

import java.util.List;

public class EnumType implements FieldType {

    public enum TypeClass implements FieldTypeClass {
        INSTANCE;

        @Override
        public String getId() {
            return "enumerated";
        }

        @Override
        public String getLabel() {
            return "Enumerated Values";
        }

        @Override
        public FieldType createType(Record typeParameters) {
            return new EnumType();
        }

        @Override
        public FieldType createType() {
            return new EnumType();
        }

        @Override
        public FormClass getParameterFormClass() {
            return null;
        }
    }

    private final Cardinality cardinality;
    private final List<EnumValue> values;

    public EnumType() {
        this.cardinality = Cardinality.SINGLE;
        this.values = Lists.newArrayList();
    }

    public EnumType(Cardinality cardinality, List<EnumValue> values) {
        this.cardinality = cardinality;
        this.values = values;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public List<EnumValue> getValues() {
        return values;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TypeClass.INSTANCE;
    }

    @Override
    public Record getParameters() {
        return new Record();
    }

    @Override
    public ComponentReader<String> getStringReader(String fieldName, String componentId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(String name, String componentId) {
        throw new UnsupportedOperationException();
    }
}
