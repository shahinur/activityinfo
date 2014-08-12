package org.activityinfo.core.shared.expr.functions;

import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.activityinfo.model.type.primitive.BooleanType;

import java.util.List;

public abstract class ComparisonOperator extends ExprFunction {

    private final String name;

    public ComparisonOperator(String name) {
        this.name = name;
    }

    @Override
    public final String getId() {
        return name;
    }

    @Override
    public final String getLabel() {
        return name;
    }

    @Override
    public FieldValue apply(List<FieldValue> arguments) {
        FieldValue a = arguments.get(0);
        FieldValue b = arguments.get(1);

        return BooleanFieldValue.valueOf(apply(a, b));

    }

    @Override
    public FieldType getResultType(List<FieldType> argumentTypes) {
        return BooleanType.INSTANCE;
    }

    protected abstract boolean apply(FieldValue a, FieldValue b);
}
