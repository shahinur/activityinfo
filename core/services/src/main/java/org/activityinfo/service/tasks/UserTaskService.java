package org.activityinfo.service.tasks;

import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

public interface UserTaskService {
    UserTask startTask(AuthenticatedUser user, String description);

    void updateTask(AuthenticatedUser user, String taskId, UserTaskStatus status);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<UserTask> getUserTasks(@InjectParam AuthenticatedUser user);
}
