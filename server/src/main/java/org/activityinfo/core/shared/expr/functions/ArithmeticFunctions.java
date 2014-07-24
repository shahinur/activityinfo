package org.activityinfo.core.shared.expr.functions;

import org.activityinfo.core.shared.expr.ExprFunction;

public class ArithmeticFunctions {

    public static final ExprFunction<Double, Double> BINARY_PLUS = new BinaryInfixFunction<Double>("+") {

        @Override
        public Double applyReal(Double x, Double y) {
            return x + y;
        }
    };

    public static final ExprFunction<Double, Double> DIVIDE = new BinaryInfixFunction<Double>("/") {

        @Override
        public Double applyReal(Double x, Double y) {
            return x / y;
        }
    };

    public static ExprFunction<Double, Double> getBinaryInfix(String name) {
        if (name.equals("+")) {
            return BINARY_PLUS;

        } else if (name.equals("/")) {
            return DIVIDE;

        } else {
            throw new IllegalArgumentException();
        }
    }
}
