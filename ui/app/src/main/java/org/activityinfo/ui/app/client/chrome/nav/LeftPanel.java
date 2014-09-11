package org.activityinfo.ui.app.client.chrome.nav;

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.tree.NavigationComponent;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class LeftPanel extends VComponent<LeftPanel> {

    private final Application app;

    public LeftPanel(Application app) {
        this.app = app;
    }

    @Override
    protected VTree render() {
        return div(LEFTPANEL, logoPanel(), innerPanel(app));
    }

    private static VNode logoPanel() {
        return div(LOGOPANEL, h1(span("["), H.t(" activityinfo "), span("]")));
    }

    private static VTree innerPanel(Application application) {
         return H.div(LEFTPANELINNER,
             new SidebarTitle(I18N.CONSTANTS.workspaces()),
             new WorkspaceSelector(application),
             new NavigationComponent(application));
    }
}
