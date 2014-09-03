package org.activityinfo.ui.app.client.page.folder;

import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.ResourceTree;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.ui.app.client.page.Breadcrumb;
import org.activityinfo.ui.app.client.page.PageStore;
import org.activityinfo.ui.flux.store.LoadingStatus;
import org.activityinfo.ui.flux.store.StoreEventBus;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.Collections;
import java.util.List;

public class FolderPage implements PageStore {

    private ResourceTree tree;
    private ResourceNode folder;
    private StoreEventBus eventBus;
    private RemoteStoreService service;


    public FolderPage(ResourceTree tree) {
        this.tree = tree;
        this.folder = tree.getRootNode();
    }

    public FolderPage(StoreEventBus eventBus, RemoteStoreService service) {
        this.eventBus = eventBus;
        this.service = service;
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
    public VTree getView() {
        return new FolderView(this);
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


    public List<ResourceNode> getChildNodes() {
        return folder.getChildren();
    }
}
