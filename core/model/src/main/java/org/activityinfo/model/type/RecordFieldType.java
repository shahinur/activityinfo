package org.activityinfo.model.type;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceIdPrefixType;

public class RecordFieldType implements FieldType {

    public interface TypeClass extends ParametrizedFieldTypeClass, RecordFieldTypeClass {}

    public static final TypeClass TYPE_CLASS = new TypeClass() {

        @Override
        public String getId() {
            return "record";
        }

        @Override
        public RecordFieldType deserializeType(Record parameters) {
            return new RecordFieldType(Types.readReference(parameters, "class"));
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
}
