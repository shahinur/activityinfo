package org.activityinfo.ui.app.client.request;

import org.activityinfo.client.ActivityInfoAsyncClient;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.RecordBeanClass;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.tasks.TaskModel;
import org.activityinfo.service.tasks.UserTask;

public class StartTask implements Request<UserTask> {

    private final String taskId;
    private final TaskModel taskModel;
    private final Record taskModelRecord;

    public <T extends TaskModel> StartTask(T taskModel, RecordBeanClass<T> recordBeanClass) {
        this.taskId = Resources.generateId().asString();
        this.taskModel = taskModel;
        this.taskModelRecord = recordBeanClass.toRecord(taskModel);
    }

    public String getTaskId() {
        return taskId;
    }

    @Override
    public Promise<UserTask> send(ActivityInfoAsyncClient service) {
        return service.startTask(taskModelRecord);
    }


    public Record getTaskModelRecord() {
        return taskModelRecord;
    }
}
