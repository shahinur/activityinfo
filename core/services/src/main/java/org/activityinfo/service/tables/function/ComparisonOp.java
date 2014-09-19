package org.activityinfo.service.tables.function;

import org.activityinfo.model.expr.functions.ExprFunction;

/**
 * Boolean-valued functions that operate on column views
 */
public enum ComparisonOp {

    EQUALS {
        @Override
        public boolean apply(int sign) {
            return sign == 0;
        }
    },
    NOT_EQUALS {
        @Override
        public boolean apply(int sign) {
            return sign != 0;
        }
    },
    LESS_THAN {
        @Override
        public boolean apply(int sign) {
            return sign < 0;
        }
    },
    GREATER_THAN {
        @Override
        public boolean apply(int sign) {
            return sign > 0;
        }
    },
    LESS_THAN_EQUAL_TO {
        @Override
        public boolean apply(int sign) {
            return sign <= 0;
        }
    },
    GREATER_THAN_EQUAL_TO {
        @Override
        public boolean apply(int sign) {
            return sign >= 0;
        }
    };


    public abstract boolean apply(int sign);

    public static ComparisonOp valueOf(ExprFunction fn) {
        switch(fn.getId()) {
            case "==":
                return EQUALS;
            case "!=":
                return NOT_EQUALS;
            case "<":
                return LESS_THAN;
            case ">":
                return GREATER_THAN;
            case "<=":
                return LESS_THAN_EQUAL_TO;
            case ">=":
                return GREATER_THAN_EQUAL_TO;
        }
        throw new IllegalArgumentException(fn.getId());
    }

}
