package org.activityinfo.model.type;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceIdPrefixType;

public class RecordFieldType implements ParametrizedFieldType {



    public interface TypeClass extends ParametrizedFieldTypeClass, RecordFieldTypeClass<Record> {}

    public static final TypeClass TYPE_CLASS = new TypeClass() {

        @Override
        public String getId() {
            return "record";
        }

        @Override
        public RecordFieldType deserializeType(Record parameters) {
            return new RecordFieldType(Types.readReference(parameters, "formClass"));
        }

        @Override
        public FormClass getParameterFormClass() {
            return new FormClass(ResourceIdPrefixType.TYPE.id(getId()));
        }

        @Override
        public Record deserialize(Record record) {
            return record;
        }

        @Override
        public FieldType createType() {
            throw new UnsupportedOperationException();
        }
    };

    private ResourceId classId;

    public RecordFieldType(ResourceId classId) {
        this.classId = classId;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    public ResourceId getClassId() {
        return classId;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitSubForm(field, this);
    }

    @Override
    public Record getParameters() {
        return Records.builder(getTypeClass())
                .set("formClass", new ReferenceValue(classId))
                .build();
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }
}
