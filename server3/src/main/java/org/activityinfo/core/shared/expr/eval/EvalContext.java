package org.activityinfo.core.shared.expr.eval;

import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;

/**
 * Provides the context for the evaluation of an expression, including symbol lookup.
 */
public interface EvalContext {

    FieldValue resolveSymbol(String symbolName);

    FieldType resolveSymbolType(String name);
}
