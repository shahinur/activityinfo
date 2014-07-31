package org.activityinfo.core.shared.expr;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class FunctionCallNode<T, K> extends ExprNode<T> {

    @Nonnull
    private ExprFunction<T, K> function;

    @Nonnull
    private List<ExprNode<K>> arguments;

    public FunctionCallNode(ExprFunction<T, K> function, List<ExprNode<K>> arguments) {
        super();
        this.function = function;
        this.arguments = arguments;
    }

    public FunctionCallNode(ExprFunction<T, K> function, ExprNode<K>... arguments) {
        this(function, Arrays.asList(arguments));
    }

    @Override
    public T evalReal() {
        return function.applyReal(arguments);
    }

    @Nonnull
    public ExprFunction<T, K> getFunction() {
        return function;
    }

    @Nonnull
    public List<ExprNode<K>> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return asExpression();
    }

    @Override
    public String asExpression() {
        return arguments.get(0) + "" + function.getId() + "" + arguments.get(1);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((arguments == null) ? 0 : arguments.hashCode());
        result = prime * result
                + ((function == null) ? 0 : function.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FunctionCallNode other = (FunctionCallNode) obj;
        return other.function.equals(function) && other.arguments.equals(arguments);
    }
}
