package org.activityinfo.model.type.barcode;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.TypeRegistry;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;

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
    public ComponentReader<String> getStringReader(final String fieldName, String componentId) {
        return new ComponentReader<String>() {
            @Override
            public String read(Resource resource) {
                Record record = resource.isRecord(fieldName);
                if(record != null) {
                    FieldValue value = TypeRegistry.get().deserializeFieldValue(record);
                    if(value instanceof BarcodeValue) {
                        return ((BarcodeValue) value).getCode();
                    }
                }
                return null;
            }
        };
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
