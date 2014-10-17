package org.activityinfo.ui.app.client.request;

import org.activityinfo.client.ActivityInfoAsyncClient;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.tasks.UserTask;

import java.util.List;

public class FetchTaskRequest implements Request<List<UserTask>> {
    @Override
    public Promise<List<UserTask>> send(ActivityInfoAsyncClient service) {
        return service.getTasks();
    }
}
