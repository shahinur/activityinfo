package org.activityinfo.model.expr.eval;

import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldValue;

public class FieldBinding implements FieldReader {
    private FormField field;
    private FieldReader reader;

    public FieldBinding(FormField field, FieldReader reader) {
        this.field = field;
        this.reader = reader;
    }

    public FormField getField() {
        return field;
    }

    public FieldReader getReader() {
        return reader;
    }

    @Override
    public FieldValue readField(Record record) {
        return reader.readField(record);
    }

    @Override
    public FieldType getType() {
        return reader.getType();
    }
}
