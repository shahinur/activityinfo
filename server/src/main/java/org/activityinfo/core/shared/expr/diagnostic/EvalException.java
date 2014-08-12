package org.activityinfo.core.shared.expr.diagnostic;

public class EvalException extends RuntimeException {

    public EvalException() {
    }

    public EvalException(String message) {
        super(message);
    }
}
