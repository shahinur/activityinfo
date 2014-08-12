package org.activityinfo.core.shared.expr.eval;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;

public interface ValueSource {

    FieldValue getValue(Resource instance, EvalContext context);

    FieldType resolveType(EvalContext context);

}
