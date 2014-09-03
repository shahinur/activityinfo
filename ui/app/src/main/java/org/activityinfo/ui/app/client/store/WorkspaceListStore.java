package org.activityinfo.ui.app.client.store;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.ui.app.client.chrome.FailureDescription;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.ui.flux.store.LoadingStatus;
import org.activityinfo.ui.flux.store.RemoteStore;
import org.activityinfo.ui.flux.store.StoreEventBus;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Maintains a list of workspaces which the user owns or is shared with.
 *
 */
public class WorkspaceListStore implements RemoteStore {

    private static final Logger LOGGER = Logger.getLogger(WorkspaceListStore.class.getName());

    private RemoteStoreService remoteStore;
    private List<ResourceNode> nodes = Lists.newArrayList();

    private final StoreEventBus eventBus;

    private LoadingStatus status = LoadingStatus.PENDING;
    private FailureDescription failureDescription;

    public WorkspaceListStore(StoreEventBus storeEventBus, RemoteStoreService remoteStore) {
        this.remoteStore = remoteStore;
        this.eventBus = storeEventBus;
    }

    public void load() {
        remoteStore.queryRoots().then(new AsyncCallback<List<ResourceNode>>() {
            @Override
            public void onFailure(Throwable caught) {

                LOGGER.log(Level.SEVERE, "Failed to load workspaces", caught);
                failureDescription = FailureDescription.of(caught);

                status = LoadingStatus.FAILED;
                eventBus.fireChange(WorkspaceListStore.this);
            }

            @Override
            public void onSuccess(List<ResourceNode> result) {
                status = LoadingStatus.LOADED;
                nodes = result;
                eventBus.fireChange(WorkspaceListStore.this);
            }
        });
    }

    public List<ResourceNode> get() {
        return nodes;
    }

    public LoadingStatus getLoadingStatus() {
        return status;
    }

    public FailureDescription getLoadingFailureDescription() {
        Preconditions.checkState(failureDescription != null);
        return failureDescription;
    }
}
