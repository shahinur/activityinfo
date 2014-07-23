package org.activityinfo.core.shared.expr;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class FunctionCallNode<T> extends ExprNode<T> {

    @Nonnull
    private ExprFunction<T> function;

    @Nonnull
    private List<ExprNode<T>> arguments;

    public FunctionCallNode(ExprFunction<T> function, List<ExprNode<T>> arguments) {
        super();
        this.function = function;
        this.arguments = arguments;
    }

    public FunctionCallNode(ExprFunction<T> function, ExprNode<T>... arguments) {
        this(function, Arrays.asList(arguments));
    }

    @Override
    public T evalReal() {
        return function.applyReal(arguments);
    }

    @Override
    public String toString() {
        return arguments.get(0) + " " + function.getName() + " " + arguments.get(1);
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
