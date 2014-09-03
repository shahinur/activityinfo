package org.activityinfo.ui.app.client.chrome.nav;

import com.google.common.collect.Lists;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.ui.app.client.page.create.NewWorkspacePlace;
import org.activityinfo.ui.app.client.page.folder.FolderPlace;
import org.activityinfo.ui.app.client.store.Router;
import org.activityinfo.ui.app.client.store.WorkspaceListStore;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.Children.toArray;
import static org.activityinfo.ui.vdom.shared.html.H.classNames;
import static org.activityinfo.ui.vdom.shared.html.H.ul;

public class WorkspaceSelector extends VThunk {

    private WorkspaceListStore workspaces;

    public WorkspaceSelector(WorkspaceListStore workspaces, Router router) {
        this.workspaces = workspaces;
    }

    @Override
    protected VTree render() {

        // Add navigation links for each of the user's workspaces
        List<VTree> items = Lists.newArrayList();
        for (ResourceNode node : workspaces.get()) {
            NavLink link = new NavLink();
            link.setLabel(node.getLabel());
            link.setUrl(Router.uri(new FolderPlace(node.getId())));
            link.setIcon(FontAwesome.TH_LARGE);
            items.add(link);
        }

        NavLink addLink = new NavLink();
        addLink.setLabel(I18N.CONSTANTS.newWorkspace());
        addLink.setUrl(Router.uri(new NewWorkspacePlace()));
        addLink.setIcon(FontAwesome.PLUS_SQUARE);
        items.add(addLink);


        return ul(classNames(NAV, NAV_PILLS, NAV_STACKED, NAV_BRACKET), toArray(items));
    }
}
