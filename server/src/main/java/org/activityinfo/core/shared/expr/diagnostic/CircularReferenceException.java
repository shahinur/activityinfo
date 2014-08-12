package org.activityinfo.core.shared.expr.diagnostic;

public class CircularReferenceException extends EvalException {

    public CircularReferenceException() {
    }

    public CircularReferenceException(String symbol) {
        super(symbol);
    }
}
