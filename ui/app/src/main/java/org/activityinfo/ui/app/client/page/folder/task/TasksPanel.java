package org.activityinfo.ui.app.client.page.folder.task;

import com.google.common.collect.Lists;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Panel;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class TasksPanel extends VComponent {

    private List<TaskButton> tasks = Lists.newArrayList();
    private String heading;

    public TasksPanel(String heading, Task... tasks) {
        this.heading = heading;
        for(Task task : tasks) {
            this.tasks.add(new TaskButton(task));
        }
    }

    @Override
    protected VTree render() {
        return new Panel(heading, content());
    }

    private VNode content() {
        return ul(BaseStyles.LIST_UNSTYLED, map(tasks, new Render<TaskButton>() {
            @Override
            public VTree render(TaskButton item) {
                return li(item);
            }
        }));
    }
}
