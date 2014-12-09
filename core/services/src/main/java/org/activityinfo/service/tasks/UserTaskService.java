package org.activityinfo.service.tasks;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.record.Record;

import java.util.List;

/**
 * Provides users with the status of background tasks running on their behalf.
 */
public interface UserTaskService {


    /**
     * Starts a new task on behalf of the given user.
     * @param user the user for whom the task should run
     * @param taskId the client-generated id of the task (unique for a given user)
     * @param taskModel the task model describing the task
     * @return a new {@code UserTask} object with status
     */
    UserTask start(AuthenticatedUser user, String taskId, Record taskModel);

    /**
     * Gets a list of running and recently completed background tasks.
     */
    List<UserTask> getUserTasks(AuthenticatedUser user);

    UserTask getUserTask(AuthenticatedUser user, String taskId);

}
