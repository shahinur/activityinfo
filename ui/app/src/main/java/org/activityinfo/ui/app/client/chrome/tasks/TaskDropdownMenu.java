package org.activityinfo.ui.app.client.chrome.tasks;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.action.AcknowledgeNotification;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.ClickHandler;
import org.activityinfo.ui.style.DropdownButton;
import org.activityinfo.ui.style.DropdownMenuItem;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.t;

public class TaskDropdownMenu extends VComponent implements StoreChangeListener {

    private final Application application;
    private final TaskIcon taskIcon;
    private final TaskBadge taskBadge;
    private DropdownButton dropdownButton;

    public TaskDropdownMenu(Application application) {
        this.application = application;
        taskIcon = new TaskIcon(application.getTaskStore());
        taskBadge = new TaskBadge(application.getTaskStore());
        dropdownButton = new DropdownButton();
        dropdownButton.getToggle().setContent(taskIcon, taskBadge);
        dropdownButton.getMenu().setTitle(I18N.CONSTANTS.tasks());
        dropdownButton.getMenu().updateItems(renderTaskMenuItems());
    }

    @Override
    protected void componentWillMount() {
        application.getTaskStore().addChangeListener(this);
    }

    @Override
    public void onStoreChanged(Store store) {
        taskIcon.refresh();
        taskBadge.refresh();
        dropdownButton.getMenu().updateItems(renderTaskMenuItems());
    }

    @Override
    protected void componentWillUnmount() {
        application.getTaskStore().removeChangeListener(this);
    }

    @Override
    protected VTree render() {
        return dropdownButton;
    }

    private List<DropdownMenuItem> renderTaskMenuItems() {
        TaskStore taskStore = application.getTaskStore();
        List<DropdownMenuItem> menuItems = Lists.newArrayList();
        for(final UserTask task : taskStore.getTasks()) {
            try {
                final TaskView taskView = TaskViews.get(task);

                DropdownMenuItem menuItem = new DropdownMenuItem();
                menuItem.setName(t(taskView.getName(task)));
                menuItem.setMessage(t(statusMessage(task)));
                menuItem.setThumbnail(taskIcon(taskView, task));
                menuItem.setNewItem(taskStore.isNewlyCompleted(task));
                menuItem.setClickHandler(new ClickHandler() {
                    @Override
                    public void onClicked() {
                        taskView.onClick(application, task);
                        application.getDispatcher().dispatch(new AcknowledgeNotification(task.getId()));
                    }
                });
                menuItems.add(menuItem);
            } catch(Throwable caught) {
                GWT.log("Exception caught while rendering task", caught);
            }
        }
        return menuItems;
    }

    private VTree taskIcon(TaskView taskView, UserTask task) {
        switch(task.getStatus()) {
            default:
            case RUNNING:
                return new VNode(HtmlTag.SPAN, PropMap.withClasses("fa fa-lg fa-cog fa-spin"));
            case FAILED:
                return new VNode(HtmlTag.SPAN, PropMap.withClasses(FontAwesome.EXCLAMATION_TRIANGLE.getClassNames() + " fa-lg"));
            case COMPLETE:
                return new VNode(HtmlTag.SPAN, PropMap.withClasses(taskView.getCompleteIcon(task).getClassNames() + " fa-lg"));
        }
    }

    private String statusMessage(UserTask task) {
        return TaskViews.get(task).getMessage(task);
    }

}
