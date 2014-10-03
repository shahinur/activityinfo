package org.activityinfo.ui.app.client.chrome.tasks;

import org.activityinfo.model.record.RecordBeanClass;
import org.activityinfo.service.tasks.TaskModel;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.vdom.shared.html.Icon;

/**
 * Provides TaskModel-specific interface and actions
 */
public interface TaskView<T extends TaskModel> {

    RecordBeanClass<T> getBeanClass();

    Icon getCompleteIcon(UserTask userTask);

    String getName(UserTask userTask);

    String getMessage(UserTask userTask);

    void onClick(Application application, UserTask task);
}
