package org.activityinfo.ui.app.client.page;

import org.activityinfo.ui.app.client.store.Status;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.div;

public class PagePreLoader extends VComponent {
    private Status<?> status;

    public PagePreLoader(Status<?> status) {
        this.status = status;
    }

    @Override
    protected VTree render() {
        return div("Loading...");
    }
}
