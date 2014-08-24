package org.activityinfo.ui.app.client.chrome;

import com.google.common.collect.Lists;
import com.google.gwt.safehtml.shared.SafeUri;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.ui.app.client.page.resource.ResourcePageContainer;
import org.activityinfo.ui.app.client.store.AppStores;
import org.activityinfo.ui.app.client.store.WorkspaceListStore;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.Children.toArray;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class LeftPanel extends VThunk<LeftPanel> {

    private final AppStores app;

    public LeftPanel(AppStores app) {
        this.app = app;
    }

    @Override
    protected VTree render(VThunk previous) {
        return null;
    }

    public static VNode leftPanel(AppStores AppStores) {
        return div(LEFTPANEL, logoPanel(), innerPanel(AppStores));
    }


    private static VNode logoPanel() {
        return div(LOGOPANEL, h1(span("["), H.t(" activityinfo "), span("]")));
    }

    private static VTree innerPanel(AppStores appStores) {
         return div(LEFTPANELINNER,

                 sidebarTitle("Workspaces"),
                 navigationMenu(appStores.getWorkspaceStore()));
    }

    private static VNode sidebarTitle(String title) {
        return new VNode(HtmlTag.H5, PropMap.withClasses(SIDEBARTITLE), t(title));
    }

    private static VNode navigationMenu(WorkspaceListStore workspaces) {

        // Add navigation links for each of the user's workspaces
        List<VTree> items = Lists.newArrayList();
        for (ResourceNode node : workspaces.get()) {
            SafeUri uri = ResourcePageContainer.uri(node.getId());
            Icon icon = FontAwesome.TH_LARGE;
            String label = node.getLabel();
            items.add( navItem(uri, icon, label) );
        }

        return ul(classNames(NAV, NAV_PILLS, NAV_STACKED, NAV_BRACKET), toArray(items));
    }


    private static VNode navItem(SafeUri url, Icon icon, String label) {
        return li(link(url, icon.render(), space(), span(label)));
    }
}
