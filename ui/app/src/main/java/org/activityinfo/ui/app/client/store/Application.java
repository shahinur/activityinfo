package org.activityinfo.ui.app.client.store;

import org.activityinfo.ui.app.client.chrome.FailureDescription;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.LoadingStatus;
import org.activityinfo.ui.flux.store.StoreEventBus;

public class Application {

    private final StoreEventBus storeEventBus;
    private final Router router;
    private final WorkspaceListStore workspaceStore;
    private final RemoteStoreService store;
    private final Dispatcher dispatcher;

    public Application(RemoteStoreService remoteStoreService) {
        storeEventBus = new StoreEventBus();
        router = new Router(this);
        workspaceStore = new WorkspaceListStore(storeEventBus, remoteStoreService);
        store = remoteStoreService;
        dispatcher = new Dispatcher();
    }

    public StoreEventBus getStoreEventBus() {
        return storeEventBus;
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

    public RemoteStoreService getStore() {
        return store;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }
}
