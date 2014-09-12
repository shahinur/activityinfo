package org.activityinfo.service.cubes;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.activityinfo.model.analysis.DimensionModel;
import org.activityinfo.model.analysis.DimensionSource;
import org.activityinfo.model.analysis.MeasureModel;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.type.time.TemporalType;
import org.activityinfo.service.tables.TableQueryBatchBuilder;

import java.util.List;
import java.util.Map;

public class FlowMeasureBuilder implements MeasureBuilder {

    private final MeasureModel model;
    private int measureIndex;
    private FormClass formClass;

    private Supplier<ColumnView> value;

    private final List<DimensionModel> dimensions;
    private final List<Supplier<ColumnView>> dimensionViews;

    public FlowMeasureBuilder(MeasureModel measureModel, int measureIndex, List<DimensionModel> dimensions, FormClass formClass) {
        this.model = measureModel;
        this.measureIndex = measureIndex;
        this.formClass = formClass;
        this.dimensions = dimensions;
        this.dimensionViews = Lists.newArrayList();
    }

    @Override
    public void scheduleRequests(TableQueryBatchBuilder batch) {

        this.value = batch.addExpression(formClass, ColumnType.NUMBER, model.getValueExpression());

        for (int i = 0; i < dimensions.size(); i++) {
            Optional<DimensionSource> source = dimensions.get(i).getSource(model.getSourceId());
            if(source.isPresent()) {
                dimensionViews.add(batch.addExpression(formClass, ColumnType.STRING, source.get().getExpression()));
            } else {
                dimensionViews.add(null);
            }
        }
    }

    private boolean isTemporal(FormField field) {
        return field.getType() instanceof TemporalType;
    }


    @Override
    public void aggregate(Map<BucketKey, Bucket> bucketMap) {
        int numRows = value.get().numRows();

        ColumnView measureView = value.get();
        ColumnView[] dimViews = buildDimensionViewArray();
        String[] dimValues = new String[dimViews.length];

        // First we have to aggregate our measure to the dimensions
        BucketMap<SumAggregator> aggregateMap = new BucketMap<>(SumAggregator.SUPPLIER);
        for(int i=0;i!=numRows;++i) {
            double value = measureView.getDouble(i);
            if(!Double.isNaN(value)) {
                // fill dim array
                for(int j=0;j < dimValues.length; ++j) {
                    if(dimViews[j] != null) {
                        dimValues[j] = dimViews[j].getString(i);
                    }
                }
                // update bucket
                aggregateMap.get(dimValues).update(value);
            }
        }

        // Now insert result into the global result set
        for (Map.Entry<BucketKey, SumAggregator> entry : aggregateMap.entrySet()) {
            BucketKey key = entry.getKey();
            Bucket bucket = bucketMap.get(key);
            if(bucket == null) {
                bucket = new Bucket(key.getDimensionValues());
                bucketMap.put(key, bucket);
            }
            bucket.setMeasure(measureIndex, entry.getValue().getResult());
        }
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

    private void dump() {

    }
}
