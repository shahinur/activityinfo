package org.activityinfo.model.type.barcode;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.RecordFieldTypeClass;
import org.activityinfo.model.type.TypeFieldType;

/**
 * A value types that describes a real-valued barcode and its units.
 */
public class BarcodeType implements FieldType {

    public interface TypeClass extends RecordFieldTypeClass<BarcodeValue> {};

    public static final TypeClass TYPE_CLASS = new TypeClass() {

        public static final String TYPE_ID = "BARCODE";

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
            return INSTANCE;
        }

        @Override
        public BarcodeValue deserialize(Record record) {
            return BarcodeValue.fromRecord(record);
        }
    };

    public static final BarcodeType INSTANCE = new BarcodeType();

    private BarcodeType() {
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitBarcodeField(field, this);
    }

    @Override
    public String toString() {
        return "BarcodeType";
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }

}
