package org.activityinfo.ui.app.client.page;

import org.activityinfo.ui.style.NotFoundPanel;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class NotFoundPageView extends PageView {
    @Override
    protected VTree render() {
        return new NotFoundPanel();
    }
}
