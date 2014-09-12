package org.activityinfo.model.type;

import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;

import java.util.List;

public class ListFieldType implements ParametrizedFieldType {


    public interface TypeClass extends ParametrizedFieldTypeClass, RecordFieldTypeClass<ListFieldValue> {}

    public static final TypeClass TYPE_CLASS = new TypeClass() {

        @Override
        public FieldType deserializeType(Record parameters) {
            return new ListFieldType(Types.<FieldType>read(parameters, "elementType", TypeFieldType.TYPE_CLASS));
        }

        @Override
        public FormClass getParameterFormClass() {
            FormClass formClass = new FormClass(Types.parameterFormClassId(this));
            formClass.addElement(new FormField(ResourceId.valueOf("elementType"))
                .setType(TypeFieldType.INSTANCE)
                .setRequired(true)
                .setLabel("Element Type"));

            return formClass;
        }

        @Override
        public ListFieldValue deserialize(Record record) {
            List<Record> elements = record.getRecordList("elements");
            List<FieldValue> values = Lists.newArrayList();
            // lists of primitives, strings, etc??
            // is that relevant here?
            for(Record element : elements) {
                values.add(TypeRegistry.get().deserializeFieldValue(element));
            }
            return ListFieldValue.valueOf(values);
        }

        @Override
        public String getId() {
            return "list";
        }

        @Override
        public String getLabel() {
            return "List";
        }

        @Override
        public FieldType createType() {
            throw new UnsupportedOperationException();
        }
    };

    public ListFieldType(FieldType elementType) {
        this.elementType = elementType;
    }

    private FieldType elementType;

    @Override
    public Record getParameters() {
        return new Record().set("elementType", elementType.asRecord());
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return null;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }
}
