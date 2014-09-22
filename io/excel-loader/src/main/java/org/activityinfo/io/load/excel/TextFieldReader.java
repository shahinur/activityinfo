package org.activityinfo.io.load.excel;

import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.primitive.TextValue;

public class TextFieldReader implements FieldReader {
    private ResourceId fieldId;
    private ColumnBuffer buffer;

    public TextFieldReader(ResourceId fieldId, ColumnBuffer buffer) {
        this.fieldId = fieldId;
        this.buffer = buffer;
    }

    @Override
    public void read(FormInstance instance, int rowIndex) {
        instance.set(fieldId, TextValue.valueOf(buffer.getString(rowIndex)));
    }
}
