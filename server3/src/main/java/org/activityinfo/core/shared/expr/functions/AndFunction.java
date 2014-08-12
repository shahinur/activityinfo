package org.activityinfo.core.shared.expr.functions;

public class AndFunction extends BinaryBooleanOperator {

    public static final AndFunction INSTANCE = new AndFunction();

    public static final String NAME = "&&";

    private AndFunction() {
        super(NAME);
    }

    @Override
    protected boolean apply(boolean a, boolean b) {
        return a && b;
    }
}
