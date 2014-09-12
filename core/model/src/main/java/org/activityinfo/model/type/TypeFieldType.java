package org.activityinfo.model.type;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;

public class TypeFieldType implements FieldType {


    public interface TypeClass extends RecordFieldTypeClass<FieldType> {  }

    public static final TypeClass TYPE_CLASS = new TypeClass() {

        @Override
        public String getId() {
            return "type";
        }

        @Override
        public FieldType deserialize(Record record) {

            String typeClassId = record.getString("typeClass");
            FieldTypeClass typeClass = TypeRegistry.get().getTypeClass(typeClassId);
            FieldType type;
            if(typeClass instanceof ParametrizedFieldTypeClass) {
                return ((ParametrizedFieldTypeClass)typeClass).deserializeType(record.getRecord("parameters"));
            } else {
                return typeClass.createType();
            }
        }


        @Override
        public String getLabel() {
            return "Type";
        }

        @Override
        public FieldType createType() {
            throw new UnsupportedOperationException();
        }
    };

    public static final TypeFieldType INSTANCE = new TypeFieldType();

    private TypeFieldType() {

    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return null;
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }

    public static Record asRecord(FieldType type) {
        Record record = new Record();
        record.set("typeClass", type.getTypeClass().getId());
        if(type instanceof ParametrizedFieldType) {
            record.set("parameters", ((ParametrizedFieldType)type).getParameters());
        }
        return record;
    }

}
