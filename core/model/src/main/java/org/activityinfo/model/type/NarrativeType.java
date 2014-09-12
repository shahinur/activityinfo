package org.activityinfo.model.type;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;

/**
 * Value type that represents a FieldType containing paragraph-like text.
 *
 */
public class NarrativeType implements FieldType {


    public static final NarrativeType INSTANCE = new NarrativeType();

    public interface TypeClass extends RecordFieldTypeClass<NarrativeValue> {}

    public static final TypeClass TYPE_CLASS = new TypeClass() {
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
        public NarrativeValue deserialize(Record record) {
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
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitNarrativeField(field, this);
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }

}
