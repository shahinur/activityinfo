package org.activityinfo.core.shared.expr.eval;

import org.activityinfo.core.shared.expr.ExprNode;
import org.activityinfo.core.shared.expr.ExprParser;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.type.CalculatedFieldType;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldValue;

public class CalculatedField implements ValueSource {

    private final ExprNode expr;

    public CalculatedField(FormField field) {
        CalculatedFieldType type = (CalculatedFieldType) field.getType();
        expr = ExprParser.parse(type.getExpression());
    }

    @Override
    public FieldValue getValue(Resource instance, EvalContext context) {
        return expr.evaluate(context);
    }

    @Override
    public FieldType resolveType(EvalContext context) {
        return expr.resolveType(context);
    }
}
