package org.activityinfo.service.tables.views;

import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormEvalContext;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.columns.DoubleArrayColumnView;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.number.Quantity;

import java.util.List;

public class DoubleColumnBuilder implements ColumnViewBuilder {

    private final String fieldName;
    private final List<Double> values = Lists.newArrayList();

    private DoubleArrayColumnView result = null;

    public DoubleColumnBuilder(ResourceId fieldId) {
        this.fieldName = fieldId.asString();
    }

    @Override
    public void accept(FormEvalContext resource) {
        double value = Double.NaN;
        FieldValue fieldValue = resource.getFieldValue(fieldName);
        if(fieldValue instanceof Quantity) {
            Quantity quantity = (Quantity) fieldValue;
            value = quantity.getValue();
        }
        values.add(value);
    }

    @Override
    public void finalizeView() {

        double array[] = new double[values.size()];
        for(int i=0;i!=array.length;++i) {
            array[i] = values.get(i);
        }
        this.result = new DoubleArrayColumnView(array);
    }

    @Override
    public ColumnView get() {
        if(result == null) {
            throw new IllegalStateException();
        }
        return result;
    }
}
