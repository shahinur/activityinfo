package org.activityinfo.ui.app.client.chrome.tasks;

import org.activityinfo.ui.style.Badges;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VText;
import org.activityinfo.ui.vdom.shared.tree.VTree;


public class TaskBadge extends VComponent {

    private TaskStore taskStore;

    public TaskBadge(TaskStore taskStore) {
        this.taskStore = taskStore;
    }

    @Override
    protected VTree render() {

        int newCount = taskStore.getNewCount();
        if(newCount == 0) {
            return VText.EMPTY_TEXT;
        } else {
            return Badges.badge(newCount);
        }
    }
}
