package org.activityinfo.service.tasks;

import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Provides users with the status of background tasks running on their behalf.
 */
@Path("/service/tasks")
public interface UserTaskService {


    /**
     * Adds a new background task to the user's list of running tasks.
     */
    UserTask startTask(AuthenticatedUser user, String description);

    /**
     * Updates the status of a user task
     */
    void updateTask(AuthenticatedUser user, String taskId, UserTaskStatus status);

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
}
