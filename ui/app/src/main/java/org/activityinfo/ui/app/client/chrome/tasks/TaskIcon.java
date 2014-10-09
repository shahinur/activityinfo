package org.activityinfo.ui.app.client.chrome.tasks;

import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.*;


public class TaskIcon extends VComponent {

    private final TaskStore taskStore;

    public TaskIcon(TaskStore taskStore) {
        this.taskStore = taskStore;
    }

    @Override
    protected VTree render() {
        if(taskStore.getRunningCount() > 0) {
            return spinningCog();
        } else {
            return new VNode(HtmlTag.SPAN, PropMap
                    .withClasses("fa fa-cog")
                    .setStyle(new Style().set("color", "#BBB")));
        }
    }

    private VNode spinningCog() {
        return new VNode(HtmlTag.SPAN, PropMap.withClasses("fa fa-cog fa-spin"));
    }
}
