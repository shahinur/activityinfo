package org.activityinfo.model.expr;

import org.activityinfo.model.expr.eval.EvalContext;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldValue;

/**
 * symbol.symbol
 */
public class CompoundExpr extends ExprNode {
    private SymbolExpr value;
    private ExprNode field;

    public CompoundExpr(SymbolExpr value, SymbolExpr field) {
        this.value = value;
        this.field = field;
    }

    @Override
    public FieldValue evaluate(EvalContext context) {
        return null;
    }

    @Override
    public FieldType resolveType(EvalContext context) {
        return null;
    }

    @Override
    public String asExpression() {
        return null;
    }

    @Override
    public <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitCompoundExpr(this);
    }
}
