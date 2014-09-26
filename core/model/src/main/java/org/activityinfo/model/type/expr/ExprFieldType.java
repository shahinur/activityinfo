package org.activityinfo.model.type.expr;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.*;

/**
 * Value type that represents an expression
 */
public class ExprFieldType implements FieldType {

    public interface TypeClass extends SingletonTypeClass, RecordFieldTypeClass<ExprValue> {
        ExprValue deserialize(Record record);
    }

    public static final TypeClass TYPE_CLASS = new TypeClass() {
        @Override
        public String getId() {
            return "expr";
        }

        @Override
        public String getLabel() {
            return "Expression";
        }

        @Override
        public FieldType createType() {
            return INSTANCE;
        }

        @Override
        public ExprValue deserialize(Record record) {
            return ExprValue.fromRecord(record);
        }
    };

    public static final ExprFieldType INSTANCE = new ExprFieldType();


    private ExprFieldType() {
    }


    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitExprField(field, this);
    }

    @Override
    public Record asRecord() {
        return TypeFieldType.asRecord(this);
    }

}
