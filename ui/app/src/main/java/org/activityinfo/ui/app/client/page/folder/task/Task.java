package org.activityinfo.ui.app.client.page.folder.task;

import org.activityinfo.ui.style.ClickHandler;
import org.activityinfo.ui.vdom.shared.html.Icon;

public interface Task extends ClickHandler {

    String getLabel();

    Icon getIcon();

}
