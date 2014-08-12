package org.activityinfo.core.shared.expr.functions;

public class OrFunction extends BinaryBooleanOperator {

    public static final ExprFunction INSTANCE = new OrFunction();

    public static final String NAME = "||";

    private OrFunction() {
        super(NAME);
    }

    @Override
    protected boolean apply(boolean a, boolean b) {
        return a || b;
    }
}
