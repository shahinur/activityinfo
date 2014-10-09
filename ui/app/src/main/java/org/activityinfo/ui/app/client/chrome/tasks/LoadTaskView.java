package org.activityinfo.ui.app.client.chrome.tasks;

import org.activityinfo.model.record.RecordBeanClass;
import org.activityinfo.service.tasks.LoadTaskModel;
import org.activityinfo.service.tasks.LoadTaskModelClass;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;

public class LoadTaskView implements TaskView<LoadTaskModel> {
    @Override
    public RecordBeanClass<LoadTaskModel> getBeanClass() {
        return LoadTaskModelClass.INSTANCE;
    }

    @Override
    public Icon getCompleteIcon(UserTask userTask) {
        return FontAwesome.CHECK_SQUARE_O;
    }

    @Override
    public String getName(UserTask userTask) {
        return "Import";
    }

    @Override
    public String getMessage(UserTask userTask) {
        return "Importing...";
    }

    @Override
    public void onClick(Application application, UserTask task) {

    }
}
