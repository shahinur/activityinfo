package org.activityinfo.model.expr.diagnostic;

public class SymbolNotFoundException extends EvalException {

    public SymbolNotFoundException() {
    }

    public SymbolNotFoundException(String symbol) {
        super("Could not resolve symbol [" + symbol + "]");
    }
}
