package org.activityinfo.model.expr.diagnostic;

public class CircularReferenceException extends EvalException {

    public CircularReferenceException() {
    }

    public CircularReferenceException(String symbol) {
        super(symbol);
    }
}
