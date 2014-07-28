package org.activityinfo.core.shared.expr;

import java.util.List;

public abstract class ExprFunction<T, K> {

    /**
     * Returns string representation of function (as it is used in expression, e.g. + - ||)
     *
     * @return string representation of function (as it is used in expression)
     */
    public abstract String getId();

    public abstract String getLabel();

    /**
     * Apply this function to the provided arguments.
     */
    public abstract T applyReal(List<ExprNode<K>> arguments);

    @Override
    public String toString() {
        return getId();
    }
}
