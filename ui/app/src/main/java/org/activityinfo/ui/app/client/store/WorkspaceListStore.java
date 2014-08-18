package org.activityinfo.ui.app.client.store;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.ui.app.client.view.chrome.FailureDescription;
import org.activityinfo.ui.store.remote.client.RemoteStoreService;
import org.activityinfo.ui.vdom.client.flux.store.LoadingStatus;
import org.activityinfo.ui.vdom.client.flux.store.RemoteStore;
import org.activityinfo.ui.vdom.client.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.client.flux.store.StoreEventEmitter;

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

    private final StoreEventEmitter emitter = new StoreEventEmitter();

    private LoadingStatus status = LoadingStatus.PENDING;
    private FailureDescription failureDescription;

    public WorkspaceListStore(RemoteStoreService remoteStore) {
        this.remoteStore = remoteStore;
    }

    public void load() {
        remoteStore.queryRoots().then(new AsyncCallback<List<ResourceNode>>() {
            @Override
            public void onFailure(Throwable caught) {

                LOGGER.log(Level.SEVERE, "Failed to load workspaces", caught);
                failureDescription = FailureDescription.of(caught);

                status = LoadingStatus.FAILED;
                emitter.fireChange(WorkspaceListStore.this);
            }

            @Override
            public void onSuccess(List<ResourceNode> result) {
                status = LoadingStatus.LOADED;
                nodes = result;
                emitter.fireChange(WorkspaceListStore.this);
            }
        });
    }

    public List<ResourceNode> get() {
        return nodes;
    }

    @Override
    public void addChangeListener(StoreChangeListener listener) {
        emitter.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(StoreChangeListener listener) {
        emitter.removeChangeListener(listener);
    }

    public LoadingStatus getLoadingStatus() {
        return status;
    }

    public FailureDescription getLoadingFailureDescription() {
        Preconditions.checkState(failureDescription != null);
        return failureDescription;
    }
}
