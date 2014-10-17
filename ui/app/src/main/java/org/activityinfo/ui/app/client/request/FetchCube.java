package org.activityinfo.ui.app.client.request;

import org.activityinfo.client.ActivityInfoAsyncClient;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.promise.Promise;

import java.util.List;

public class FetchCube implements Request<List<Bucket>> {

    private final PivotTableModel cubeModel;

    public FetchCube(PivotTableModel cubeModel) {
        this.cubeModel = cubeModel;
    }

    @Override
    public Promise<List<Bucket>> send(ActivityInfoAsyncClient service) {
        return service.queryCube(cubeModel);
    }
}
