package org.activityinfo.service.tables.views;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.columns.DoubleArrayColumnView;
import org.activityinfo.service.tables.reader.DoubleFieldReader;

import java.util.List;

public class DoubleColumnBuilder implements ColumnViewBuilder {

    private final DoubleFieldReader reader;
    private final List<Double> values = Lists.newArrayList();

    private DoubleArrayColumnView result = null;

    public DoubleColumnBuilder(DoubleFieldReader reader) {
        this.reader = reader;
    }

    @Override
    public void putResource(Resource resource) {
        double value = reader.readDouble(resource);
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
