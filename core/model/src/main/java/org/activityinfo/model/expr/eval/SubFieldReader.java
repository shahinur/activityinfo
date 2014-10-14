package org.activityinfo.model.expr.eval;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldValue;

public class SubFieldReader implements FieldReader {

    private FieldReader parent;
    private FieldReader child;

    public SubFieldReader(FieldReader parent, FieldReader child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public FieldValue readField(Record record) {
        Record parentValue = (Record) parent.readField(record);
        if(parentValue == null) {
            return null;
        }
        return child.readField(parentValue);
    }

    @Override
    public FieldType getType() {
        return child.getType();
    }
}
