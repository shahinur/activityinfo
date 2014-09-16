package org.activityinfo.ui.app.client.page;

import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.div;

public class PagePreLoader extends VComponent {

    public PagePreLoader() {
    }

    @Override
    protected VTree render() {
        return div("Loading...");
    }
}
