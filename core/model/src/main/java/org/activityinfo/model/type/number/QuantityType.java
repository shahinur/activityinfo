package org.activityinfo.model.type.number;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.*;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.primitive.TextType;

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
        public QuantityType createType() {
            return new QuantityType()
                    .setUnits("households");
        }

        @Override
        public QuantityType deserializeType(Record typeParameters) {
            QuantityType type = new QuantityType();
            type.setUnits(typeParameters.isString("units"));

            EnumFieldValue aggregationValue = Types.read(typeParameters, "aggregation", EnumType.TYPE_CLASS);
            if(aggregationValue != null) {
                type.setAggregation(AggregationType.valueOf(aggregationValue.getValueId().asString()));
            }
            return type;
        }

        @Override
        public FormClass getParameterFormClass() {
            FormClass formClass = new FormClass(Types.parameterFormClassId(this));
            formClass.addElement(new FormField(ResourceId.valueOf("units"))
                    .setType(TextType.INSTANCE)
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
    private AggregationType aggregation = AggregationType.SUM;

    public QuantityType() {
    }

    public QuantityType(String units) {
        this.units = units;
    }

    public String getUnits() {
        return units;
    }

    public QuantityType setUnits(String units) {
        this.units = units;
        return this;
    }

    public AggregationType getAggregation() {
        return aggregation;
    }

    public QuantityType setAggregation(AggregationType aggregation) {
        this.aggregation = aggregation;
        return this;
    }

    @Override
    public ParametrizedFieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public Record getParameters() {
        return Records.builder(getTypeClass())
                .set("units", units)
                .set("aggregation", new EnumFieldValue(aggregation.name()))
                .build();
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
