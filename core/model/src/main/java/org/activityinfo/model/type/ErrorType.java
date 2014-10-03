package org.activityinfo.model.type;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.Records;

public class ErrorType implements FieldType {

    public static final FieldTypeClass TYPE_CLASS = new FieldTypeClass() {
        @Override
        public String getId() {
            return "ERR";
        }

        @Override
        public FieldType createType() {
            throw new UnsupportedOperationException();
        }
    };

    public static final ErrorType INSTANCE = new ErrorType();

    private ErrorType() {
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
    }

    @Override
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Record asRecord() {
        return Records.builder(TYPE_CLASS).build();
    }
}
