package org.activityinfo.store.tasks;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.tasks.TaskContext;

public interface TaskContextProvider {

    TaskContext create(AuthenticatedUser user);
}
