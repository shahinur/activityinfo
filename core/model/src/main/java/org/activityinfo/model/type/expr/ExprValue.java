package org.activityinfo.model.type.expr;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.record.IsRecord;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;

/**
 * A FieldValue containing a symbolic expression such as "A + B"
 */
public class ExprValue implements FieldValue, IsRecord {

    private final String expression;

    public ExprValue(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return ExprFieldType.TYPE_CLASS;
    }

    @Override
    public Record asRecord() {
        return Records.builder()
                .set(FieldValue.TYPE_CLASS_FIELD_NAME, getTypeClass().getId())
                .set("value", expression)
                .build();
    }

    public static ExprValue valueOf(String value) {
        return new ExprValue(value);
    }

    public static ExprValue fromRecord(Record record) {
        return new ExprValue(record.getString("value"));
    }
}
