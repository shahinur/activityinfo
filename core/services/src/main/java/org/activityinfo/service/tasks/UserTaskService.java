package org.activityinfo.service.tasks;

import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.record.Record;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Provides users with the status of background tasks running on their behalf.
 */
@Path("/service/tasks")
public interface UserTaskService {


    String TASK_NAME_HEADER = "X-AppEngine-TaskName";

    /**
     * Adds a new background task to the user's list of running tasks.
     */
    UserTask startTask(AuthenticatedUser user, String description);

    /**
     * Updates the status of a user task
     */
    void updateTask(AuthenticatedUser user, String taskId, UserTaskStatus status);

    @POST
    @Path("{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    UserTask start(@InjectParam AuthenticatedUser user, @PathParam("taskId") String taskId, Record taskModel);

    /**
     * Gets a list of running and recently completed background tasks.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<UserTask> getUserTasks(@InjectParam AuthenticatedUser user);


    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    UserTask getUserTask(@InjectParam AuthenticatedUser user, @PathParam("id") String taskId);

    @POST
    @Path("run")
    Response run(@HeaderParam(TASK_NAME_HEADER) String taskName,
                 @FormParam("userId") int userId,
                 @FormParam("taskId") String taskId);
}
