package org.activityinfo.core.shared.expr;

public enum TokenType {


    /**
     * "("
     */
    PAREN_START(false),

    /**
     * ")"
     */
    PAREN_END(false),

    /**
     * "{"
     */
    BRACE_START(false),

    /**
     * "}"
     */
    BRACE_END(false),

    /**
     * Operator : +, /, -
     */
    OPERATOR(true),

    /**
     * A named value
     */
    SYMBOL(true),

    FUNCTION(true),

    /**
     * Numeric Literal
     */
    NUMBER(false),

    /**
     * Whitespace
     */
    WHITESPACE(false),

    EOF(false);

    private final boolean isSymbol;

    TokenType(boolean isSymbol) {
        this.isSymbol = isSymbol;
    }

    public boolean isSymbol() {
        return isSymbol;
    }
}
