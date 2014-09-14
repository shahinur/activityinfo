package org.activityinfo.service.tables.views;

import org.activityinfo.model.form.FormEvalContext;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.primitive.BooleanFieldValue;

import java.util.BitSet;

public class BooleanColumnBuilder implements ColumnViewBuilder {

    private final ResourceId fieldId;
    private BitSet values = new BitSet();
    private BitSet missing = new BitSet();
    private int index = 0;

    public BooleanColumnBuilder(ResourceId fieldId) {
        this.fieldId = fieldId;
    }

    @Override
    public void accept(FormEvalContext instance) {
        FieldValue fieldValue = instance.getFieldValue(fieldId);
        if(fieldValue instanceof BooleanFieldValue) {
            values.set(index, fieldValue == BooleanFieldValue.TRUE);
        } else {
            missing.set(index, true);
        }
        index++;
    }

    @Override
    public void finalizeView() {

    }

    @Override
    public ColumnView get() {
        return null;
    }
}
