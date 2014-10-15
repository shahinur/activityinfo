package org.activityinfo.service.tasks.appengine;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.tasks.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/service/executeTask")
public class TaskRunner {

    private static final Logger LOGGER = Logger.getLogger(TaskRunner.class.getName());

    public static final String TASK_NAME_HEADER = "X-AppEngine-TaskName";

    private final TaskStore store;
    private final TaskExecutors executors;
    private final TaskContextProvider contextProvider;


    @Inject
    public TaskRunner(TaskStore store, TaskExecutors executors, TaskContextProvider contextProvider) {
        this.store = store;
        this.executors = executors;
        this.contextProvider = contextProvider;

    }

    @POST
    public Response run(@HeaderParam(TASK_NAME_HEADER) String taskName,
                        @FormParam("userId") int userId,
                        @FormParam("taskId") String taskId) {

        AuthenticatedUser user = new AuthenticatedUser(userId);
        LOGGER.info(String.format("Task id %s for user %d starting.", taskId, user.getId()));

        // Ensure that random people on the internet do not trigger tasks
        assertAuthorizedRequest(taskName);

        UserTask task = assertTaskPresent(taskId, user);
        assertNotCompleted(task);

        // Obtain the executor
        TaskExecutor executor = executors.get(task);
        TaskModel taskModel = executors.deserializeModel(task.getTaskModel());

        // Kick off the task
        try {
            LOGGER.info("Starting task");
            TaskContext context = contextProvider.create(user);
            executor.execute(context, taskModel);

            // mark task as complete
            LOGGER.info("Task completed successfully");
            store.updateTask(user, taskId, UserTaskStatus.COMPLETE);

        } catch (NoClassDefFoundError | Exception e) {

            LOGGER.log(Level.SEVERE, "Exception thrown during task execution", e);

            store.updateTask(user, taskId, UserTaskStatus.FAILED);
        }

        return Response.ok().build();
    }

    public void assertAuthorizedRequest(String taskName) {
        if(Strings.isNullOrEmpty(taskName)) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }

    public UserTask assertTaskPresent(String taskId, AuthenticatedUser user) {
        UserTask task;
        try {
            task = store.get(user, taskId);
        } catch (EntityNotFoundException e) {
            throw abortTask(e, "Entity with %s does not exist", taskId);
        }
        return task;
    }

    public void assertNotCompleted(UserTask task) {
        if(task.getStatus() != UserTaskStatus.RUNNING) {
            LOGGER.info(String.format("Task status is %s, exiting.", task.getStatus().name()));
            throw new WebApplicationException(Response.Status.OK);
        }
    }

    private RuntimeException abortTask(Throwable cause, String message, Object... args) {

        LOGGER.log(Level.SEVERE, String.format(message, args), cause);
        // We have to return a 2xx code, otherwise AppEngine will continue to retry
        // task indefinitely.
        throw new WebApplicationException(Response.ok().build());
    }
}
