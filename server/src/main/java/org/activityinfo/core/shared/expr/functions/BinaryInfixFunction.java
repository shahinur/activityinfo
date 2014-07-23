package org.activityinfo.core.shared.expr.functions;

import org.activityinfo.core.shared.expr.ExprFunction;
import org.activityinfo.core.shared.expr.ExprNode;

import java.util.List;

public abstract class BinaryInfixFunction<T> extends ExprFunction<T> {

    private String symbol;

    public BinaryInfixFunction(String symbol) {
        super();
        this.symbol = symbol;
    }

    @Override
    public final String getName() {
        return symbol;
    }

    @Override
    public T applyReal(List<ExprNode<T>> arguments) {
        return applyReal(arguments.get(0).evalReal(), arguments.get(1).evalReal());
    }

    public abstract T applyReal(T x, T y);
}
