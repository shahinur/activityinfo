package org.activityinfo.ui.app.client.request;

import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.RemoteStoreService;

import java.util.List;

public class FetchCube implements Request<List<Bucket>> {

    private final PivotTableModel cubeModel;

    public FetchCube(PivotTableModel cubeModel) {
        this.cubeModel = cubeModel;
    }

    @Override
    public Promise<List<Bucket>> send(RemoteStoreService service) {
        return service.queryCube(cubeModel);
    }
}
