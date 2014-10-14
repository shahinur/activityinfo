package org.activityinfo.model.expr.eval;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldValue;

/**
* Created by alex on 10/13/14.
*/
class ConstantFieldReader implements FieldReader {

    private final FieldValue value;
    private final FieldType type;

    ConstantFieldReader(FieldValue value, FieldType type) {
        this.value = value;
        this.type = type;
    }


    @Override
    public FieldValue readField(Record record) {
        return value;
    }

    @Override
    public FieldType getType() {
        return type;
    }
}
