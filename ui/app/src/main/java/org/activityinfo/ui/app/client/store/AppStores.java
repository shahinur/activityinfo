package org.activityinfo.ui.app.client.store;

import org.activityinfo.ui.app.client.view.chrome.FailureDescription;
import org.activityinfo.ui.store.remote.client.RemoteStoreService;
import org.activityinfo.ui.vdom.client.flux.store.LoadingStatus;

public class AppStores {

    private Router router;
    private WorkspaceListStore workspaceStore;

    public AppStores(RemoteStoreService remoteStoreService) {
        router = new Router(remoteStoreService);
        workspaceStore = new WorkspaceListStore(remoteStoreService);
    }

    public Router getRouter() {
        return router;
    }

    public WorkspaceListStore getWorkspaceStore() {
        return workspaceStore;
    }


    public LoadingStatus getLoadingStatus() {
        return workspaceStore.getLoadingStatus();
    }

    public FailureDescription getLoadingFailureDescription() {
        return workspaceStore.getLoadingFailureDescription();

    }
}
