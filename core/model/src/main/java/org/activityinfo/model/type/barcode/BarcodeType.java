package org.activityinfo.model.type.barcode;

import org.activityinfo.model.type.*;

/**
 * A value types that describes a real-valued barcode and its units.
 */
public class BarcodeType implements FieldType {


    public static final FieldTypeClass TYPE_CLASS = new FieldTypeClass() {

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
    };

    public static final BarcodeType INSTANCE = new BarcodeType();

    private BarcodeType() {
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public String toString() {
        return "BarcodeType";
    }
}
