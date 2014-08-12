package org.activityinfo.core.shared.expr.functions;

public class BooleanFunctions {
    public static final ExprFunction AND = AndFunction.INSTANCE;
    public static final ExprFunction OR = OrFunction.INSTANCE;
    public static final ExprFunction EQUAL = EqualFunction.INSTANCE;
    public static final ExprFunction NOT_EQUAL = NotEqualFunction.INSTANCE;
}
