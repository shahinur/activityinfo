package org.activityinfo.service.cubes;

import org.activityinfo.model.table.Bucket;
import org.activityinfo.service.tables.TableQueryBatchBuilder;

import java.util.Map;

public interface MeasureBuilder {


    void scheduleRequests(TableQueryBatchBuilder batch);

    void aggregate(Map<BucketKey, Bucket> bucketMap);
}
