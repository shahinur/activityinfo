package org.activityinfo.model.type.number;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;


public class QuantityType implements FieldType {


    public static enum TypeClass implements FieldTypeClass {

        INSTANCE {
            @Override
            public String getId() {
                return "QUANTITY";
            }

            @Override
            public QuantityType createType(Record typeParameters) {
                return new QuantityType()
                        .setUnits(typeParameters.getString("units"));
            }

            @Override
            public FormClass getParameterFormClass() {
                FormClass formClass = new FormClass(ResourceId.create("_quantity"));
                formClass.addField("units")
                        .setLabel("Units")
                        .setDescription("Describes the unit of measurement. For example: 'households', 'individuals'," +
                                " 'meters', etc.");
                return formClass;
            }
        }
    }

    private Record record = new Record();

    public QuantityType() {
    }

    public String getUnits() {
        return record.getString("units");
    }

    public QuantityType setUnits(String units) {
        record.set("units", units);
        return this;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TypeClass.INSTANCE;
    }

    @Override
    public Record getParameters() {
        return record;
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
