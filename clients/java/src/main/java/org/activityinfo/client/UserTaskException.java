package org.activityinfo.client;

import org.activityinfo.service.tasks.UserTask;

public class UserTaskException extends RuntimeException {

    private UserTask task;

    public UserTaskException(UserTask task, String message) {
        super(message);
        this.task = task;
    }

    public UserTask getTask() {
        return task;
    }
}
