package org.activityinfo.model.type.expr;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.RecordFieldTypeClass;
import org.activityinfo.model.type.SingletonTypeClass;

/**
 * Value type that represents an expression
 */
public class ExprFieldType implements FieldType {

    public interface TypeClass extends SingletonTypeClass, RecordFieldTypeClass {
        ExprValue deserialize(Record record);
    }

    public static final TypeClass TYPE_CLASS = new TypeClass() {
        @Override
        public String getId() {
            return "expr";
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
}
