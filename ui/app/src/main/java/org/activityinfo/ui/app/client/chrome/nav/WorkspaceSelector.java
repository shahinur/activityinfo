package org.activityinfo.ui.app.client.chrome.nav;

import com.google.common.collect.Lists;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.page.create.NewWorkspacePlace;
import org.activityinfo.ui.app.client.page.folder.FolderPlace;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.Children.toArray;
import static org.activityinfo.ui.vdom.shared.html.H.classNames;
import static org.activityinfo.ui.vdom.shared.html.H.ul;

public class WorkspaceSelector extends VComponent implements StoreChangeListener {

    private final Application application;

    public WorkspaceSelector(Application application) {
        this.application = application;
    }

    @Override
    public void componentDidMount() {
        application.getWorkspaceStore().addChangeListener(this);
    }

    @Override
    protected void componentWillUnmount() {
        application.getWorkspaceStore().removeChangeListener(this);
    }

    @Override
    public void onStoreChanged(Store store) {
        refresh();
    }

    @Override
    protected VTree render() {

        // Add navigation links for each of the user's workspaces
        List<VTree> items = Lists.newArrayList();
        for (ResourceNode node : application.getWorkspaceStore().get()) {
            NavLink link = new NavLink(application.getRouter());
            link.setLabel(node.getLabel());
            link.setTarget(new FolderPlace(node.getId()));
            link.setIcon(FontAwesome.TH_LARGE);
            items.add(link);
        }

        NavLink addLink = new NavLink(application.getRouter());
        addLink.setLabel(I18N.CONSTANTS.newWorkspace());
        addLink.setTarget(NewWorkspacePlace.INSTANCE);
        addLink.setIcon(FontAwesome.PLUS_SQUARE);
        items.add(addLink);

        return ul(classNames(NAV, NAV_PILLS, NAV_STACKED, NAV_BRACKET), toArray(items));
    }
}
