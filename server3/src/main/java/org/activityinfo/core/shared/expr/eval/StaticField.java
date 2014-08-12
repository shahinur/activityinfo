package org.activityinfo.core.shared.expr.eval;

import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.FieldValues;

public class StaticField implements ValueSource {

    private FormField field;

    public StaticField(FormField field) {
        this.field = field;
    }

    @Override
    public FieldValue getValue(Resource instance, EvalContext context) {
        return FieldValues.readFieldValueIfType(
                instance,
                field.getId().asString(),
                field.getType().getTypeClass());
    }

    @Override
    public FieldType resolveType(EvalContext context) {
        return field.getType();
    }
}
