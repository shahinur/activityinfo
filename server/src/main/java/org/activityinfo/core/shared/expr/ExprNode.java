package org.activityinfo.core.shared.expr;

/**
 * Root of the expression hierarchy. Expressions are used for validation and
 * calculation by AI
 */
public abstract class ExprNode<T> {

    /**
     * Evaluates the expression to a real value.
     */
    public abstract T evalReal();

    public abstract String asExpression();

}
