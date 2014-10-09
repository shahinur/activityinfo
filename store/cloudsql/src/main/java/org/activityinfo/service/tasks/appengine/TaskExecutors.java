package org.activityinfo.service.tasks.appengine;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import org.activityinfo.io.load.LoadTaskExecutor;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.tasks.*;
import org.activityinfo.service.tasks.appengine.export.ExportFormExecutor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Map;

@Singleton
public class TaskExecutors {
    private final Map<ResourceId, TaskExecutor> executors = Maps.newHashMap();

    public TaskExecutors() {
        executors.put(ExportFormTaskModelClass.CLASS_ID, new ExportFormExecutor());
        executors.put(LoadTaskModelClass.CLASS_ID, new LoadTaskExecutor());
    }

    public TaskExecutor<?> get(UserTask task) {
        return get(task.getTaskModel());
    }

    public TaskExecutor<?> get(Record taskModel) {
        TaskExecutor<?> executor = executors.get(taskModel.getClassId());
        if(executor == null) {

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                .entity("No executor for model class " + taskModel.getClassId())
                .build());
        }
        return executor;
    }
    public TaskModel deserializeModel(Record taskModel) {
        TaskExecutor<?> executor = executors.get(taskModel.getClassId());
        return executor.getModelClass().toBean(taskModel);
    }

}
