package org.activityinfo.service.cubes;

import org.activityinfo.service.tables.TableQueryBatchBuilder;

public interface MeasureBuilder {


    void scheduleRequests(TableQueryBatchBuilder batch);

    void aggregate(TupleCollector collector);
}
