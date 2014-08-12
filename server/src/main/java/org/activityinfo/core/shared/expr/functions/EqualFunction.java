package org.activityinfo.core.shared.expr.functions;

import org.activityinfo.model.type.FieldValue;

public class EqualFunction extends ComparisonOperator {

    public static final EqualFunction INSTANCE = new EqualFunction();

    public EqualFunction() {
        super("==");
    }

    @Override
    protected boolean apply(FieldValue a, FieldValue b) {
        return a.equals(b);
    }
}
