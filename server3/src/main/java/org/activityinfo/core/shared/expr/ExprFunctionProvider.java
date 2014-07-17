package org.activityinfo.core.shared.expr;

public interface ExprFunctionProvider {

    /**
     * Provides a function by name
     *
     * @param functionName
     * @return a ExprFunction implementation
     * @throws IllegalArgumentException if no such function exists
     */
    ExprFunction get(String functionName, int numArguments);

}
