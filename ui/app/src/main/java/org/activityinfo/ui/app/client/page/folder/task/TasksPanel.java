package org.activityinfo.ui.app.client.page.folder.task;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Panel;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class TasksPanel extends VComponent {

    private List<TaskButton> tasks = Lists.newArrayList();

    public TasksPanel(Application application, ResourceId ownerId) {
        tasks.add(new TaskButton(new CreateFolderTask(application, ownerId)));
        tasks.add(new TaskButton(new CreatePivotTableTask(application, ownerId)));
    }

    @Override
    protected VTree render() {
        return new Panel("Common Tasks", ul(BaseStyles.LIST_UNSTYLED, map(tasks, new H.Render<TaskButton>() {
            @Override
            public VTree render(TaskButton item) {
                return li(item);
            }
        })));
    }
}
