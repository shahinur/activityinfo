package org.activityinfo.model.expr;

import org.activityinfo.model.expr.eval.EvalContext;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.activityinfo.model.type.primitive.TextValue;

import javax.annotation.Nonnull;

public class ConstantExpr extends ExprNode {

    @Nonnull
    private final FieldValue value;

    public ConstantExpr(@Nonnull FieldValue value) {
        this.value = value;
    }

    public ConstantExpr(double value) {
        this(new Quantity(value));
    }

    public ConstantExpr(boolean value) {
        this(BooleanFieldValue.valueOf(value));
    }

    public ConstantExpr(String value) {
        this(TextValue.valueOf(value));
    }

    @Override
    public FieldValue evaluate(EvalContext context) {
        return value;
    }

    @Nonnull
    public FieldValue getValue() {
        return value;
    }

    @Override
    public String asExpression() {
        if(value instanceof TextValue) {
            // TODO: Escaping
            return "\"" + value + "\"";
        } else if(value instanceof Quantity) {
            return Double.toString(((Quantity) value).getValue());
        } else if(value instanceof BooleanFieldValue) {
            return ((BooleanFieldValue) value).asBoolean() ? "true" : "false";
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldType resolveType(EvalContext context) {
        return value.getTypeClass().createType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConstantExpr that = (ConstantExpr) o;

        if (!value.equals(that.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }


    @Override
    public String toString() {
        return asExpression();
    }
}
