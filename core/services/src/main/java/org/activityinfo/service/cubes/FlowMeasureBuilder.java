package org.activityinfo.service.cubes;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.activityinfo.model.analysis.DimensionModel;
import org.activityinfo.model.analysis.DimensionSource;
import org.activityinfo.model.analysis.MeasureModel;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.HashMapBucket;
import org.activityinfo.service.tables.RowSetBuilder;
import org.activityinfo.service.tables.TableQueryBatchBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rolls up a measure of "flows" to the given dimension.
 *
 * Measures of flows can be aggregated across all dimensions.
 */
public class FlowMeasureBuilder implements MeasureBuilder {

    private final MeasureModel model;
    private FormClass formClass;

    private Supplier<ColumnView> value;
    private Supplier<ColumnView> criteria;

    private final List<DimensionModel> dimensions;
    private final List<Supplier<ColumnView>> dimensionViews;

    public FlowMeasureBuilder(MeasureModel measureModel, List<DimensionModel> dimensions, FormClass formClass) {
        this.model = measureModel;
        this.formClass = formClass;
        this.dimensions = dimensions;
        this.dimensionViews = Lists.newArrayList();
    }

    @Override
    public void scheduleRequests(TableQueryBatchBuilder batch) {

        RowSetBuilder rowSetBuilder = new RowSetBuilder(formClass.getId(), batch);

        this.value = rowSetBuilder.fetch(model.getValueExpression());
        this.criteria = rowSetBuilder.fetch(resolveCriteria());

        for (int i = 0; i < dimensions.size(); i++) {
            Optional<DimensionSource> source = dimensions.get(i).getSource(model.getSourceId());
            if(source.isPresent()) {
                dimensionViews.add(rowSetBuilder.fetch(source.get().getExpression()));
            } else {
                dimensionViews.add(null);
            }
        }
    }

    private String resolveCriteria() {
        if(Strings.isNullOrEmpty(model.getCriteriaExpression())) {
            return "true";
        } else {
            return model.getCriteriaExpression();
        }
    }

    @Override
    public List<HashMapBucket> aggregate() {
        int numRows = value.get().numRows();

        ColumnView measureView = value.get();
        ColumnView criteriaView = criteria.get();
        ColumnView[] dimViews = buildDimensionViewArray();
        String[] dimValues = new String[dimViews.length];

        // First we have to aggregate our measure to the dimensions
        BucketMap<SumAggregator> aggregateMap = new BucketMap<>(SumAggregator.SUPPLIER);
        for(int i=0;i!=numRows;++i) {
            if(criteriaView.getBoolean(i) == ColumnView.TRUE) {
                double value = measureView.getDouble(i);
                if (!Double.isNaN(value)) {
                    // fill dim array
                    for (int j = 0; j < dimValues.length; ++j) {
                        if (dimViews[j] != null) {
                            dimValues[j] = dimViews[j].getString(i);
                        }
                    }
                    // update bucket
                    aggregateMap.get(dimValues).update(value);
                }
            }
        }

        // Now insert result into the global result set
        List<HashMapBucket> buckets = new ArrayList<>();
        for (Map.Entry<BucketKey, SumAggregator> entry : aggregateMap.entrySet()) {
            BucketKey key = entry.getKey();
            HashMapBucket bucket = new HashMapBucket(model.getId(), entry.getValue().getResult());
            for(int i=0;i!=dimensions.size();++i) {
                bucket.setDimensionValue(dimensions.get(i).getId(), key.getDimensionValue(i));
            }
            for (Map.Entry<String, String> tag : model.getDimensionTags().entrySet()) {
                bucket.setDimensionValue(tag.getKey(), tag.getValue());
            }
            buckets.add(bucket);
        }
        return buckets;
    }

    private ColumnView[] buildDimensionViewArray() {
        ColumnView[] array = new ColumnView[dimensions.size()];
        for (int i = 0; i < dimensions.size(); i++) {
            if(dimensionViews.get(i) != null) {
                array[i] = dimensionViews.get(i).get();
            }
        }
        return array;
    }

}
