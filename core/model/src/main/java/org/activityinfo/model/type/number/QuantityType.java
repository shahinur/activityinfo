package org.activityinfo.model.type.number;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.*;

/**
 * A value types that describes a real-valued quantity and its units.
 */
public class QuantityType implements ParametrizedFieldType {


    public static class TypeClass implements ParametrizedFieldTypeClass, RecordFieldTypeClass<Quantity> {

        private TypeClass() {}

        @Override
        public String getId() {
            return "QUANTITY";
        }

        @Override
        public String getLabel() {
            return "Quantity";
        }

        @Override
        public QuantityType createType() {
            return new QuantityType()
                    .setUnits("households");
        }

        @Override
        public QuantityType deserializeType(Record typeParameters) {
            return new QuantityType()
                    .setUnits(typeParameters.getString("units"));
        }

        @Override
        public FormClass getParameterFormClass() {
            FormClass formClass = new FormClass(Types.parameterFormClassId(this));
            formClass.addElement(new FormField(ResourceId.valueOf("units"))
                    .setType(FREE_TEXT.createType())
                    .setLabel("Units")
                    .setDescription("Describes the unit of measurement. For example: 'households', 'individuals'," +
                                    " 'meters', etc."));
            return formClass;
        }

        @Override
        public Quantity deserialize(Record record) {
            return Quantity.fromRecord(record);
        }
    };

    public static final TypeClass TYPE_CLASS = new TypeClass();

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
    public ParametrizedFieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public Record getParameters() {
        return new Record()
                .set("units", units)
                .set("classId", getTypeClass().getParameterFormClass().getId());
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitQuantityField(field, this);
    }

    @Override
    public String toString() {
        return "QuantityType";
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }

}
