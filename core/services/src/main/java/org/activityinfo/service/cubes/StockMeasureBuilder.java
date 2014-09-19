package org.activityinfo.service.cubes;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.activityinfo.model.analysis.cube.MeasureMapping;
import org.activityinfo.model.analysis.cube.SourceMapping;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.type.time.TemporalType;
import org.activityinfo.service.tables.RowSetBuilder;
import org.activityinfo.service.tables.TableQueryBatchBuilder;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Rolls up a measure of "flows" to the given dimension.
 *
 * Measures of flows can be aggregated across all dimensions.
 */
public class StockMeasureBuilder implements MeasureBuilder {

    private final AttributeReaderSet attributeReaders;

    private Supplier<ColumnView> value;
    private Supplier<ColumnView> measurementTime;

    private final List<Supplier<ColumnView>> primaryKeyViews = Lists.newArrayList();
    private SourceMapping source;
    private MeasureMapping mapping;

    public StockMeasureBuilder(CubeContext context, SourceMapping source, MeasureMapping mapping) {
        this.source = source;
        this.mapping = mapping;
        this.attributeReaders = new AttributeReaderSet(context, source, mapping);
    }

    @Override
    public void scheduleRequests(TableQueryBatchBuilder batch) {

        RowSetBuilder rowSetBuilder = new RowSetBuilder(source.getSourceId(), batch);
        FormClass formClass = batch.getForm(source.getSourceId());

        this.value = rowSetBuilder.fetch(mapping.getValueExpression());
        attributeReaders.scheduleRequests(rowSetBuilder);

        // To the dimension, we have to add the primary keys in the first round
        // to ensure we don't double count multiple measurements of the same indicator at different times.
        for(FormField field : formClass.getFields()) {
            if(field.isPrimaryKey()) {
                if(field.getType() instanceof TemporalType) {
                    measurementTime = rowSetBuilder.fetch(field);
                } else {
                    primaryKeyViews.add(rowSetBuilder.fetch(field));
                }
            }
        }
        if(measurementTime == null) {
            throw new IllegalStateException("No temporal primary key found for source " + formClass.getLabel());
        }
    }

    private java.util.Collection<StockAggregator> resolve() {
        int numRows = value.get().numRows();
        ColumnView measureView = value.get();
        ColumnView timeView = measurementTime.get();
        ColumnView[] keyViews = viewArray(primaryKeyViews);

        String[] keys = new String[keyViews.length];

        attributeReaders.start();

        // First we have to aggregate our measure to the dimensions
        BucketMap<StockAggregator> aggregateMap = new BucketMap<>(StockAggregator.SUPPLIER);
        for(int i=0;i!=numRows;++i) {
            Date measurementDate = timeView.getDate(i);
            double value = measureView.getDouble(i);
            if (!Double.isNaN(value)) {
                fillRow(keyViews, keys, i);

                // update bucket
                aggregateMap.get(keys).insert(measurementDate, value, attributeReaders.read(i));
            }
        }

        for (Map.Entry<BucketKey, StockAggregator> entry : aggregateMap.entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }

        return aggregateMap.values();
    }

    private void fillRow(ColumnView[] views, String[] values, int rowIndex) {
        for (int j = 0; j < values.length; ++j) {
            if(views[j] != null) {
                values[j] = views[j].getString(rowIndex);
            }
        }
    }

    @Override
    public void aggregate(TupleCollector collector) {

        for(StockAggregator period : resolve()) {
            collector.add(period.getAverage(), mapping.getMeasureId(), period.getDimKey());
        }
    }

    private ColumnView[] viewArray(List<Supplier<ColumnView>> views) {
        ColumnView[] array = new ColumnView[views.size()];
        for (int i = 0; i < views.size(); i++) {
            if(views.get(i) != null) {
                array[i] = views.get(i).get();
            }
        }
        return array;
    }

}
