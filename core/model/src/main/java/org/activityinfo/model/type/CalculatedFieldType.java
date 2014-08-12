package org.activityinfo.model.type;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceIdPrefixType;

/**
 * A Value Type that represents a value calculated from a symbolic expression,
 * such as "A + B"
 */
public class CalculatedFieldType implements ParametrizedFieldType {

    public static final ParametrizedFieldTypeClass TYPE_CLASS = new ParametrizedFieldTypeClass() {
        @Override
        public String getId() {
            return "calculated";
        }

        @Override
        public String getLabel() {
            return "Calculated";
        }

        @Override
        public FieldType createType() {
            return new CalculatedFieldType();
        }

        @Override
        public FieldType deserializeType(Record parameters) {
            return new CalculatedFieldType(parameters.isString("expression"));
        }

        @Override
        public FormClass getParameterFormClass() {

            FormField exprField = new FormField(ResourceId.create("expression"));
            exprField.setLabel("Expression");
            exprField.setType(ExprFieldType.INSTANCE);

            FormClass formClass = new FormClass(ResourceIdPrefixType.TYPE.id(getId()));
            formClass.addElement(exprField);

            return formClass;
        }
    };

    private String expression;

    public CalculatedFieldType() {
    }

    public CalculatedFieldType(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public Record getParameters() {
        return new Record().set("expression", expression);
    }
}
