package org.activityinfo.service.tables.function;

import org.activityinfo.model.expr.functions.ExprFunction;

public enum DoubleBinaryOp {

    PLUS {
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    },
    MULTIPLY {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE {
        @Override
        public double apply(double x, double y) {
            return x / y;
        }
    };

    public abstract double apply(double x, double y);

    public static DoubleBinaryOp valueOf(ExprFunction function) {
        switch (function.getId()) {
            case "+":
                return PLUS;
            case "-":
                return MINUS;
            case "*":
                return MULTIPLY;
            case "/":
                return DIVIDE;
        }
        throw new IllegalArgumentException(function.getId());
    }
}
