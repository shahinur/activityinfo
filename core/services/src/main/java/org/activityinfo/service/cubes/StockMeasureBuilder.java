package org.activityinfo.service.cubes;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.activityinfo.model.analysis.DimensionModel;
import org.activityinfo.model.analysis.DimensionSource;
import org.activityinfo.model.analysis.MeasureModel;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.HashMapBucket;
import org.activityinfo.model.type.time.TemporalType;
import org.activityinfo.service.tables.RowSetBuilder;
import org.activityinfo.service.tables.TableQueryBatchBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Rolls up a measure of "flows" to the given dimension.
 *
 * Measures of flows can be aggregated across all dimensions.
 */
public class StockMeasureBuilder implements MeasureBuilder {

    private final MeasureModel model;
    private FormClass formClass;

    private Supplier<ColumnView> value;
    private Supplier<ColumnView> measurementTime;
    private Supplier<ColumnView> criteria;

    private final List<DimensionModel> dimensions;
    private final List<Supplier<ColumnView>> dimensionViews = Lists.newArrayList();
    private final List<Supplier<ColumnView>> primaryKeyViews = Lists.newArrayList();

    public StockMeasureBuilder(MeasureModel measureModel, List<DimensionModel> dimensions, FormClass formClass) {
        this.model = measureModel;
        this.formClass = formClass;
        this.dimensions = dimensions;
    }

    @Override
    public void scheduleRequests(TableQueryBatchBuilder batch) {

        RowSetBuilder rowSetBuilder = new RowSetBuilder(formClass.getId(), batch);

        this.value = rowSetBuilder.fetch(model.getValueExpression());
        this.criteria = rowSetBuilder.fetch(resolveCriteria());

        // Dimensions
        for (int i = 0; i < dimensions.size(); i++) {
            Optional<DimensionSource> source = dimensions.get(i).getSource(model.getSourceId());
            if(source.isPresent()) {
                dimensionViews.add(rowSetBuilder.fetch(source.get().getExpression()));
            } else {
                dimensionViews.add(null);
            }
        }

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

    private String resolveCriteria() {
        if(Strings.isNullOrEmpty(model.getCriteriaExpression())) {
            return "true";
        } else {
            return model.getCriteriaExpression();
        }
    }

    private java.util.Collection<StockAggregator> resolve() {
        int numRows = value.get().numRows();
        ColumnView measureView = value.get();
        ColumnView timeView = measurementTime.get();
        ColumnView[] keyViews = viewArray(primaryKeyViews);
        ColumnView[] dimViews = viewArray(dimensionViews);

        String[] keys = new String[keyViews.length];
        String[] dimValues = new String[dimViews.length];

        // First we have to aggregate our measure to the dimensions
        BucketMap<StockAggregator> aggregateMap = new BucketMap<>(StockAggregator.SUPPLIER);
        for(int i=0;i!=numRows;++i) {
            Date measurementDate = timeView.getDate(i);
            double value = measureView.getDouble(i);
            if (!Double.isNaN(value)) {
                fillRow(keyViews, keys, i);
                fillRow(dimViews, dimValues, i);
                // update bucket
                aggregateMap.get(keys).insert(measurementDate, value, dimValues);
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
    public List<HashMapBucket> aggregate() {

        BucketMap<SumAggregator> aggregateMap = new BucketMap<>(SumAggregator.SUPPLIER);

        for(StockAggregator period : resolve()) {
            aggregateMap.get(period.getDimKey()).update(period.getAverage());
        }

        // Now insert result into the global result set
        List<HashMapBucket> buckets = new ArrayList<>();
        for (Map.Entry<BucketKey, SumAggregator> entry : aggregateMap.entrySet()) {
            BucketKey key = entry.getKey();
            HashMapBucket bucket = new HashMapBucket(model.getId(), entry.getValue().getResult());
            for(int i=0;i!=dimensions.size();++i) {
                bucket.setDimensionValue(dimensions.get(i).getId(), key.getDimensionValue(i));
            }
            buckets.add(bucket);
        }
        return buckets;
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
