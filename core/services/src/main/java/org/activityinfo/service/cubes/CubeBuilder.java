package org.activityinfo.service.cubes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.analysis.Cube;
import org.activityinfo.model.analysis.MeasureModel;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.service.store.StoreAccessor;
import org.activityinfo.service.tables.TableQueryBatchBuilder;

import java.util.List;
import java.util.Map;

public class CubeBuilder {

    private StoreAccessor resourceStore;

    public CubeBuilder(StoreAccessor resourceStore) {
        this.resourceStore = resourceStore;
    }

    public Cube buildCube(PivotTableModel model) throws Exception {


        // Create the builders for each metric
        List<MeasureBuilder> measureBuilders = Lists.newArrayList();
        for (int i = 0; i < model.getMeasures().size(); i++) {
            MeasureModel measureModel = model.getMeasures().get(i);
            FormClass formClass = FormClass.fromResource(resourceStore.get(measureModel.getSourceId()));

            measureBuilders.add(new FlowMeasureBuilder(measureModel, i, model.getDimensions(), formClass));
        }

        // Queue requests for columns
        TableQueryBatchBuilder batch = new TableQueryBatchBuilder(resourceStore);
        for (MeasureBuilder builder : measureBuilders) {
            builder.scheduleRequests(batch);
        }

        // Execute the request
        batch.execute();

        // compute the metrics
        Map<BucketKey, Bucket>  buckets = Maps.newHashMap();
        for (MeasureBuilder builder : measureBuilders) {
            builder.aggregate(buckets);
        }

        return new Cube(model, buckets.values());
    }
}
