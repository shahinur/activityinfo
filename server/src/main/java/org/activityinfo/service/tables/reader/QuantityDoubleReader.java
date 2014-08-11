package org.activityinfo.service.tables.reader;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValues;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.number.QuantityType;

public class QuantityDoubleReader implements DoubleFieldReader {

    private String fieldName;

    public QuantityDoubleReader(ResourceId fieldId) {
        this.fieldName = fieldId.asString();
    }

    @Override
    public double readDouble(Resource resource) {
        Quantity quantity = FieldValues.readFieldValueIfType(resource, fieldName, QuantityType.TYPE_CLASS);
        if(quantity != null) {
            return quantity.getValue();
        } else {
            return Double.NaN;
        }
    }
}
