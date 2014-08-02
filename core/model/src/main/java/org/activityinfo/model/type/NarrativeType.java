package org.activityinfo.model.type;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.component.ComponentReader;

/**
 * Value type that represents a FieldType containing paragraph-like text.
 *
 */
public class NarrativeType implements FieldType {


    public static final NarrativeType INSTANCE = new NarrativeType();

    public static final FieldTypeClass TYPE_CLASS = new RecordFieldTypeClass() {
        @Override
        public String getId() {
            return "NARRATIVE";
        }

        @Override
        public String getLabel() {
            return "Multi-line Text";
        }

        @Override
        public FieldType createType() {
            return INSTANCE;
        }

        @Override
        public FieldValue deserialize(Record record) {
            return NarrativeValue.fromRecord(record);
        }
    };

    private NarrativeType() {
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
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
