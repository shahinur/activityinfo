package org.activityinfo.service.tasks;

import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.record.Record;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Provides users with the status of background tasks running on their behalf.
 */
@Path("/service/tasks")
public final class UserTaskRestfulService {


    private final UserTaskService service;

    @Inject
    public UserTaskRestfulService(UserTaskService service) {
        this.service = service;
    }

    @POST
    @Path("{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserTask start(@InjectParam AuthenticatedUser user, @PathParam("taskId") String taskId, Record taskModel) {
        return service.start(user, taskId, taskModel);
    }

    /**
     * Gets a list of running and recently completed background tasks.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserTask> getUserTasks(@InjectParam AuthenticatedUser user) {
        return service.getUserTasks(user);
    }


    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserTask getUserTask(@InjectParam AuthenticatedUser user, @PathParam("id") String taskId) {
        return service.getUserTask(user, taskId);
    }
}
