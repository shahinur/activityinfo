package org.activityinfo.model.type;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;

public class SubFormType implements FieldType {

    public interface TypeClass extends ParametrizedFieldTypeClass, RecordFieldTypeClass<SubFormValue> {}

    public static final TypeClass TYPE_CLASS = new TypeClass() {

        @Override
        public String getId() {
            return "subForm";
        }

        @Override
        public SubFormType deserializeType(Record parameters) {
            return new SubFormType(Types.readReference(parameters, "class"));
        }

        @Override
        public FormClass getParameterFormClass() {
            throw new UnsupportedOperationException("todo");
        }

        @Override
        public SubFormValue deserialize(Record record) {
            return new SubFormValue(Types.readReference(record, "class"), record.isRecord("fields"));
        }

        @Override
        public String getLabel() {
            return "Sub Form";
        }

        @Override
        public FieldType createType() {
            throw new UnsupportedOperationException();
        }
    };

    private ResourceId classId;

    public SubFormType(ResourceId classId) {
        this.classId = classId;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Record asRecord() {
        return new Record().set("class", new ReferenceValue(classId).asRecord());
    }
}
