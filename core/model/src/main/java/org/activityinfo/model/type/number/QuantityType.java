package org.activityinfo.model.type.number;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;

public class QuantityType implements FieldType {

    public static enum TypeClass implements FieldTypeClass {

        INSTANCE {
            @Override
            public String getId() {
                return "quantity";
            }

            @Override
            public QuantityType createType(Record typeParameters) {
                return new QuantityType()
                        .setUnits(typeParameters.getString("units"));
            }
        }
    }

    private String units;

    public QuantityType() {
    }

    public String getUnits() {
        return units;
    }

    public QuantityType setUnits(String units) {
        this.units = units;
        return this;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TypeClass.INSTANCE;
    }

    @Override
    public Record getParameters() {
        return new Record().set("units", units);
    }

    @Override
    public ComponentReader getStringReader(String fieldName, String componentId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(String name, String componentId) {
        return new NullComponentReader<>();
    }
}
