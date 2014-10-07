package org.activityinfo.service.tables.views;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.views.DoubleArrayColumnView;
import org.activityinfo.model.type.FieldValue;

import java.util.List;

public class DoubleColumnBuilder implements ColumnViewBuilder {

    private final String fieldName;
    private final List<Double> values = Lists.newArrayList();

    private DoubleArrayColumnView result = null;
    private DoubleReader reader;

    public DoubleColumnBuilder(ResourceId fieldId, DoubleReader reader) {
        this.reader = reader;
        this.fieldName = fieldId.asString();
    }

    @Override
    public void accept(FieldValue fieldValue) {
        values.add(reader.read(fieldValue));
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
