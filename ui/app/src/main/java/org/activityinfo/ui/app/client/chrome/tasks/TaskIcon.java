package org.activityinfo.ui.app.client.chrome.tasks;

import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class TaskIcon extends VComponent {

    private final Application application;
    private final StoreChangeListener storeChangeListener = new StoreChangeListener() {
        @Override
        public void onStoreChanged(Store store) {
            refresh();
        }
    };

    public TaskIcon(Application application) {
        this.application = application;
    }

    @Override
    protected void componentWillMount() {
        application.getTaskStore().addChangeListener(storeChangeListener);
    }

    @Override
    protected void componentWillUnmount() {
        application.getTaskStore().removeChangeListener(storeChangeListener);
    }

    @Override
    protected VTree render() {
        return div(className(BTN_GROUP),
            new VNode(HtmlTag.BUTTON, classNames(BTN, BTN_DEFAULT, DROPDOWN_TOGGLE, TP_ICON),
                icon()));
    }

    private VNode icon() {
        if(application.getTaskStore().getRunningCount() > 0) {
            return new VNode(HtmlTag.SPAN, PropMap.withClasses("fa fa-cog fa-spin"));
        } else {
            return new VNode(HtmlTag.SPAN, PropMap.withClasses("fa fa-cog"));
        }
    }
}
