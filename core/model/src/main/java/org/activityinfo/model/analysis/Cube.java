package org.activityinfo.model.analysis;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.activityinfo.model.table.Bucket;

import java.util.Collection;
import java.util.List;

public class Cube {

    private PivotTableModel model;
    private final Collection<Bucket> buckets;

    public Cube(PivotTableModel model, Collection<Bucket> values) {
        this.model = model;
        this.buckets = values;
    }

    public void dump() {

        List<String> headers = Lists.newArrayList();
        for(DimensionModel dimension : model.getDimensions()) {
            headers.add(dimension.getLabel());
        }
        for(MeasureModel measure : model.getMeasures()) {
            headers.add(measure.getLabel());
        }
        System.out.println(Joiner.on(",").join(headers));

        for(Bucket bucket : buckets) {
            List<String> cells = Lists.newArrayList();
            for (int i = 0; i < model.getDimensions().size(); i++) {
                cells.add(Strings.nullToEmpty(bucket.getDimension(i)));
            }
            for (int i = 0; i < model.getMeasures().size(); ++i) {
                double measure = bucket.getMeasure(i);
                if(Double.isNaN(measure)) {
                    cells.add("");
                } else {
                    cells.add(Double.toString(measure));
                }
            }
            System.out.println(Joiner.on(",").join(cells));
        }
    }
}
