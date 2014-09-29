package org.activityinfo.service.cubes;

import com.google.common.collect.Lists;
import org.activityinfo.model.analysis.MeasureModel;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.service.store.StoreAccessor;
import org.activityinfo.service.tables.TableQueryBatchBuilder;

import java.util.List;

public class CubeBuilder {

    private StoreAccessor resourceStore;

    public CubeBuilder(StoreAccessor resourceStore) {
        this.resourceStore = resourceStore;
    }

    public List<Bucket> buildCube(PivotTableModel model) throws Exception {


        // Create the builders for each metric
        List<MeasureBuilder> measureBuilders = Lists.newArrayList();
        for (int i = 0; i < model.getMeasures().size(); i++) {
            MeasureModel measureModel = model.getMeasures().get(i);
            FormClass formClass = FormClass.fromResource(resourceStore.get(measureModel.getSourceId()));

            switch(measureModel.getMeasurementType()) {
                case STOCK:
                    measureBuilders.add(new StockMeasureBuilder(measureModel, model.getDimensions(), formClass));
                    break;
                case FLOW:
                    measureBuilders.add(new FlowMeasureBuilder(measureModel, model.getDimensions(), formClass));
                    break;
            }
        }

        // Queue requests for columns
        TableQueryBatchBuilder batch = new TableQueryBatchBuilder(resourceStore);
        for (MeasureBuilder builder : measureBuilders) {
            builder.scheduleRequests(batch);
        }

        // Execute the request
        batch.execute();

        // compute the metrics
        List<Bucket> buckets = Lists.newArrayList();
        for (MeasureBuilder builder : measureBuilders) {
            buckets.addAll(builder.aggregate());
        }

        return buckets;
    }
}
