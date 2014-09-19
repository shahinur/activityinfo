package org.activityinfo.service.cubes;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.activityinfo.model.analysis.cube.CubeModel;
import org.activityinfo.model.analysis.cube.MeasureMapping;
import org.activityinfo.model.analysis.cube.MeasureModel;
import org.activityinfo.model.analysis.cube.SourceMapping;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.service.store.StoreAccessor;
import org.activityinfo.service.tables.TableQueryBatchBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CubeBuilder {

    private StoreAccessor resourceStore;

    public CubeBuilder(StoreAccessor resourceStore) {
        this.resourceStore = resourceStore;
    }

    public List<Bucket> buildCube(CubeModel model, List<ResourceId> attributeIds, Set<ResourceId> measureIds)
        throws Exception {

        CubeContext context = new CubeContext(model);


        // Create the builders for each metric
        List<MeasureBuilder> measureBuilders = Lists.newArrayList();
        for(SourceMapping source : model.getMappings()) {
            for(MeasureMapping measureMapping : source.getMeasureMappings()) {
                MeasureModel measure = context.getMeasure(measureMapping.getMeasureId());
                switch(measure.getMeasurementType()) {
                    case STOCK:
                        measureBuilders.add(new StockMeasureBuilder(context, source, measureMapping));
                        break;
                    case FLOW:
                        measureBuilders.add(new FlowMeasureBuilder(context, source, measureMapping));
                        break;
                }
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
        MyCollector collector = new MyCollector(context);

        for (MeasureBuilder builder : measureBuilders) {
            builder.aggregate(collector);
        }

        return buckets;
    }

    public String toString(CubeContext context, Multimap<Integer, String> tuple) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for(int i=0;i!=context.getAttributeCount();++i) {
            if(i > 0) {
                sb.append(", ");
            }
            Collection<String> members = tuple.get(i);
            if(members.size() == 1) {
                sb.append(Iterables.getOnlyElement(members));
            } else if(members.size() > 1) {
                sb.append("(");
                Joiner.on(", ").appendTo(sb, members);
                sb.append(")");
            }
        }
        sb.append(" }");
        return sb.toString();
    }


    private class MyCollector implements TupleCollector {

        private CubeContext context;

        private MyCollector(CubeContext context) {
            this.context = context;
        }

        @Override
        public void add(double value, ResourceId measureId, Multimap<Integer, String> dimensionValues) {
            System.out.println(String.format("%.0f", value) + " " +  measureId + " "  +
                CubeBuilder.this.toString(context, dimensionValues));
        }
    }
}
