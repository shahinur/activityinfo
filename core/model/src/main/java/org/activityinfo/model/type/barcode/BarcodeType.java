package org.activityinfo.model.type.barcode;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceIdPrefixType;
import org.activityinfo.model.type.*;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;
import org.activityinfo.model.type.number.Quantity;

/**
 * A value types that describes a real-valued barcode and its units.
 */
public class BarcodeType implements ParametrizedFieldType {


    public static class TypeClass implements ParametrizedFieldTypeClass, RecordFieldTypeClass {
        public static final String TYPE_ID = "BARCODE";

        private TypeClass() {}

        @Override
        public String getId() {
            return TYPE_ID;
        }

        @Override
        public String getLabel() {
            return "Barcode";
        }

        @Override
        public FieldType createType() {
            return new BarcodeType()
                    .setUnits("households");
        }

        @Override
        public BarcodeType deserializeType(Record typeParameters) {
            return new BarcodeType()
                    .setUnits(typeParameters.getString("units"));
        }

        @Override
        public FormClass getParameterFormClass() {
            FormClass formClass = new FormClass(ResourceIdPrefixType.TYPE.id("barcode"));
            formClass.addElement(new FormField(ResourceId.create("units"))
                    .setType(FREE_TEXT.createType())
                    .setLabel("Units")
                    .setDescription("Describes the unit of measurement. For example: 'school', 'households', 'individuals'," +
                                    " 'info', etc."));
            return formClass;
        }

        @Override
        public FieldValue deserialize(Record record) {
            return Quantity.fromRecord(record);
        }
    };

    public static final TypeClass TYPE_CLASS = new TypeClass();

    private String units;

    public BarcodeType() {
    }

    public String getUnits() {
        return units;
    }

    public BarcodeType setUnits(String units) {
        this.units = units;
        return this;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public Record getParameters() {
        return new Record().set("units", units);
    }

    @Override
    public ComponentReader<String> getStringReader(String fieldName, String componentId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(String name, String componentId) {
        return new NullComponentReader<>();
    }

    @Override
    public String toString() {
        return "BarcodeType";
    }
}
