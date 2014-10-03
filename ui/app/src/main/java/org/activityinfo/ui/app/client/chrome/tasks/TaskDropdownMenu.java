package org.activityinfo.ui.app.client.chrome.tasks;

import com.google.gwt.core.client.GWT;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.action.AcknowledgeNotification;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.Badges;
import org.activityinfo.ui.style.ClickHandler;
import org.activityinfo.ui.style.DropdownButton;
import org.activityinfo.ui.style.DropdownMenuItem;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.*;

import static org.activityinfo.ui.vdom.shared.html.H.t;

public class TaskDropdownMenu extends VComponent implements StoreChangeListener {

    private final Application application;

    public TaskDropdownMenu(Application application) {
        this.application = application;
    }

    @Override
    protected void componentWillMount() {
        application.getTaskStore().addChangeListener(this);
    }

    @Override
    public void onStoreChanged(Store store) {
        refresh();
    }

    @Override
    protected void componentWillUnmount() {
        application.getTaskStore().removeChangeListener(this);
    }

    @Override
    protected VTree render() {
        DropdownButton button = new DropdownButton();
        button.getToggle().setContent(toggleButton());
        button.getMenu().setTitle(I18N.CONSTANTS.tasks());

        TaskStore taskStore = application.getTaskStore();
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
                button.getMenu().add(menuItem);
            } catch(Throwable caught) {
                GWT.log("Exception caught while rendering task", caught);
            }
        }
        return button;
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

    private VNode[] toggleButton() {
        int newCount = application.getTaskStore().getNewCount();
        if(newCount == 0) {
            return new VNode[] { icon() };
        } else {
            return new VNode[] { icon(), Badges.badge(newCount)};
        }
    }

    private VNode icon() {
        if(application.getTaskStore().getRunningCount() > 0) {
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
