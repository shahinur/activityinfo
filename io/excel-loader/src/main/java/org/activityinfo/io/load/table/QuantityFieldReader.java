package org.activityinfo.io.load.table;

import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.number.Quantity;

public class QuantityFieldReader implements FieldReader {

    private ResourceId resourceId;
    private ColumnBuffer buffer;

    public QuantityFieldReader(ResourceId resourceId, ColumnBuffer buffer) {
        this.resourceId = resourceId;
        this.buffer = buffer;
    }

    @Override
    public void read(FormInstance instance, int rowIndex) {
        Object value = buffer.getRow(rowIndex);
        if(value instanceof Number) {
            instance.set(resourceId, new Quantity(((Number) value).doubleValue()));
        }
    }
}
