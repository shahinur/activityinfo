package org.activityinfo.core.shared.expr;

import java.util.List;

public abstract class ExprFunction {

    /**
     * @return the name of this function
     */
    public abstract String getName();

    /**
     * Apply this function to the provided arguments.
     */
    public abstract double applyReal(List<ExprNode> arguments);
}
