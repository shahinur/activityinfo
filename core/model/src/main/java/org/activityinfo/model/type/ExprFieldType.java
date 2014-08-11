package org.activityinfo.model.type;

/**
 * Value type that represents an expression
 */
public class ExprFieldType implements FieldType {

    public static final FieldTypeClass TYPE_CLASS = new FieldTypeClass() {
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
    };

    public static final ExprFieldType INSTANCE = new ExprFieldType();


    private ExprFieldType() {
    }


    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

}
