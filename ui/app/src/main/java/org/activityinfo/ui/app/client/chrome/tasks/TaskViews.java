package org.activityinfo.ui.app.client.chrome.tasks;

import com.google.common.collect.Maps;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.tasks.UserTask;

import java.util.Map;

public class TaskViews {

    public static TaskViews INSTANCE = null;

    private Map<ResourceId, TaskView> models = Maps.newHashMap();


    private TaskViews() {
        addTask(new ExportFormTaskView());
    }

    private void addTask(ExportFormTaskView ui) {
        models.put(ui.getBeanClass().getClassId(), ui);
    }

    public static TaskView get(UserTask task) {
        if(INSTANCE == null) {
            INSTANCE = new TaskViews();
        }

        assert task.getTaskModel() != null : "taskModel is null";

        return INSTANCE.models.get(task.getTaskModel().getClassId());
    }
}
