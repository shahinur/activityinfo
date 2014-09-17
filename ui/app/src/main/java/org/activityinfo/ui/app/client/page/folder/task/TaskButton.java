package org.activityinfo.ui.app.client.page.folder.task;

import org.activityinfo.ui.style.Button;
import org.activityinfo.ui.style.ButtonStyle;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.t;

public class TaskButton extends VComponent {
    private final Task task;
    private final Button button;

    public TaskButton(Task task) {
        this.task = task;
        this.button = new Button(ButtonStyle.LINK, task.getIcon().render(), t(" "), t(task.getLabel()));
        this.button.setClickHandler(task);
    }


    @Override
    protected VTree render() {
        return button;
    }
}
