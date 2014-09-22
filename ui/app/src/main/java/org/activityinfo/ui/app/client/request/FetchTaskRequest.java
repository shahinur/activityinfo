package org.activityinfo.ui.app.client.request;

import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.service.tasks.UserTask;

import java.util.List;

public class FetchTaskRequest implements Request<List<UserTask>> {
    @Override
    public Promise<List<UserTask>> send(RemoteStoreService service) {
        return service.getTasks();
    }
}
