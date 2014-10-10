package org.activityinfo.service.tasks.appengine;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.record.Record;
import org.activityinfo.service.tasks.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppEngineUserTaskService implements UserTaskService {

    private static final Logger LOGGER = Logger.getLogger(AppEngineUserTaskService.class.getName());

    private final TaskStore store;
    private final TaskExecutors executors;

    @Inject
    public AppEngineUserTaskService(TaskStore store, TaskExecutors executors) {
        this.store = store;
        this.executors = executors;
    }

    @POST
    @Path("{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public UserTask start(@InjectParam AuthenticatedUser user, @PathParam("taskId") String taskId, Record taskModelRecord) {

        UserTask task = createTask(user, taskId, taskModelRecord);

        // Kick off task
        QueueFactory.getDefaultQueue().add(null, TaskOptions.Builder
            .withUrl("/service/executeTask")
            .param("userId", Integer.toString(user.getId()))
            .param("taskId", task.getId()));

        return task;
    }

    @VisibleForTesting
    UserTask createTask(AuthenticatedUser user, String taskId, Record taskModelRecord) {

        // Deserialize task model
        TaskExecutor executor = executors.get(taskModelRecord);
        TaskModel taskModel = executors.deserializeModel(taskModelRecord);

        // Create a new task record
        try {
            String description;
            try {
                description = executor.describe(taskModel);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.SEVERE, "Invalid task model", e);
                throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build());
            }

            UserTask task = new UserTask();
            task.setId(taskId);
            task.setStatus(UserTaskStatus.RUNNING);
            task.setTimeStarted(System.currentTimeMillis());
            task.setTaskModel(taskModelRecord);

            store.put(user, task);

            return task;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception thrown while describing task", e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserTask> getUserTasks(@InjectParam AuthenticatedUser user) {
        return store.queryRecent(user);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public UserTask getUserTask(@InjectParam AuthenticatedUser user, @PathParam("id") String taskId) {
        try {
            return store.get(user, taskId);
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
}
