package org.activityinfo.service.cubes;

import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.activityinfo.model.analysis.cube.MeasureMapping;
import org.activityinfo.model.analysis.cube.MeasureModel;
import org.activityinfo.model.analysis.cube.SourceMapping;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.service.tables.RowSetBuilder;
import org.activityinfo.service.tables.TableQueryBatchBuilder;

import java.util.Collection;

/**
 * Rolls up a measure of "flows" to the given dimension.
 *
 * Measures of flows can be aggregated across all dimensions.
 */
public class FlowMeasureBuilder implements MeasureBuilder {

    private final CubeContext context;
    private final MeasureModel model;
    private final MeasureMapping measureMapping;
    private final SourceMapping source;
    private final AttributeReaderSet attributeReaders;

    private Supplier<ColumnView> value;


    public FlowMeasureBuilder(CubeContext context, SourceMapping source, MeasureMapping measureMapping) {
        this.context = context;
        this.model = context.getMeasure(measureMapping.getMeasureId());
        this.source = source;
        this.measureMapping = measureMapping;
        this.attributeReaders = new AttributeReaderSet(context, source, measureMapping);
    }

    @Override
    public void scheduleRequests(TableQueryBatchBuilder batch) {

        RowSetBuilder rowSetBuilder = new RowSetBuilder(source.getSourceId(), batch);
        this.value = rowSetBuilder.fetch(measureMapping.getValueExpression());
        this.attributeReaders.scheduleRequests(rowSetBuilder);
    }

    @Override
    public void aggregate(TupleCollector collector) {
        int numRows = value.get().numRows();

        ColumnView measureView = value.get();

        attributeReaders.start();

        System.out.println("Scanning " + measureMapping.getValueExpression());

        // First we have to aggregate our measure to the dimensions
        //BucketMap<SumAggregator> aggregateMap = new BucketMap<>(SumAggregator.SUPPLIER);
        for(int i=0;i!=numRows;++i) {

            double value = measureView.getDouble(i);
            if (!Double.isNaN(value)) {
                collector.add(value, measureMapping.getMeasureId(), attributeReaders.read(i));
            }
        }
    }

//    private ColumnView[] buildDimensionViewArray() {
//        ColumnView[] array = new ColumnView[dimensions.size()];
//        for (int i = 0; i < dimensions.size(); i++) {
//            if(dimensionViews.get(i) != null) {
//                array[i] = dimensionViews.get(i).get();
//            }
//        }
//        return array;
//    }

}
