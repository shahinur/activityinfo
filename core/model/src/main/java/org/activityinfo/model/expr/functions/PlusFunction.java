package org.activityinfo.model.expr.functions;

import org.activityinfo.model.type.number.Quantity;

public class PlusFunction extends RealValuedBinaryFunction {

    public static final PlusFunction INSTANCE = new PlusFunction();

    private PlusFunction() {
        super("+");
    }

    @Override
    protected double apply(double a, double b) {
        return a + b;
    }

    @Override
    protected String applyUnits(String a, String b) {
        if(a.equals(b)) {
            return a;
        } else {
            return Quantity.UNKNOWN_UNITS;
        }
    }
}
