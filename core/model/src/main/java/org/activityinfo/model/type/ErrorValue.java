package org.activityinfo.model.type;

import org.activityinfo.model.expr.diagnostic.ExprException;

/**
 * A FieldValue resulting from an {@link org.activityinfo.model.expr.diagnostic.ExprException}, indicating
 * that the user has defined a expression incorrectly.
 */
public class ErrorValue implements FieldValue {

    private final ExprException exception;

    public ErrorValue(ExprException e) {
        this.exception = e;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return MissingTypeClass.INSTANCE;
    }


    @Override
    public String toString() {
        return "#ERR";
    }
}
