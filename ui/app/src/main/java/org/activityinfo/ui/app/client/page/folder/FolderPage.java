package org.activityinfo.ui.app.client.page.folder;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.ResourceTree;
import org.activityinfo.ui.app.client.page.Breadcrumb;
import org.activityinfo.ui.app.client.page.resource.ResourcePage;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.flux.store.LoadingStatus;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.shared.html.Icon;

import java.util.Collections;
import java.util.List;

public class FolderPage implements ResourcePage {

    private ResourceTree tree;
    private ResourceNode folder;


    public FolderPage(ResourceTree tree) {
        this.tree = tree;
        this.folder = tree.getRootNode();
    }

    @Override
    public ResourceId getResourceId() {
        return folder.getId();
    }

    @Override
    public boolean tryHandleNavigation(String[] path) {
        return false;
    }

    @Override
    public String getPageTitle() {
        return folder.getLabel();
    }

    @Override
    public String getPageDescription() {
        return null;
    }

    @Override
    public Icon getPageIcon() {
        return FontAwesome.FOLDER_OPEN_O;
    }

    @Override
    public List<Breadcrumb> getBreadcrumbs() {
        return Collections.emptyList();
    }

    @Override
    public void stop() {

    }

    @Override
    public void start() {

    }

    @Override
    public LoadingStatus getLoadingStatus() {
        return LoadingStatus.LOADED;
    }

    @Override
    public void addChangeListener(StoreChangeListener listener) {

    }

    @Override
    public void removeChangeListener(StoreChangeListener listener) {

    }

    public List<ResourceNode> getChildNodes() {
        return folder.getChildren();
    }
}
