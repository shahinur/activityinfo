package org.activityinfo.ui.app.client.chrome.nav;

import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.SIDEBARTITLE;
import static org.activityinfo.ui.vdom.shared.html.H.t;

public class SidebarTitle extends VComponent<SidebarTitle> {

    private final String title;

    public SidebarTitle(String title) {
        this.title = title;
    }
    
    @Override
    protected VTree render() {
        return new VNode(HtmlTag.H5, PropMap.withClasses(SIDEBARTITLE), t(title));
    }
}
