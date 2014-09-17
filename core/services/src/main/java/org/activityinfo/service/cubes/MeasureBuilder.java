package org.activityinfo.service.cubes;

import org.activityinfo.model.table.HashMapBucket;
import org.activityinfo.service.tables.TableQueryBatchBuilder;

import java.util.List;

public interface MeasureBuilder {


    void scheduleRequests(TableQueryBatchBuilder batch);

    List<HashMapBucket> aggregate();
}
