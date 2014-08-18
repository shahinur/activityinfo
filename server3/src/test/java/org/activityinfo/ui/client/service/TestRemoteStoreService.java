package org.activityinfo.ui.client.service;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.ResourceTree;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.ResourceTreeRequest;
import org.activityinfo.ui.store.remote.client.RemoteStoreService;

import java.util.List;

public class TestRemoteStoreService implements RemoteStoreService {

    private final ResourceStore store;

    public TestRemoteStoreService(ResourceStore store) {
        this.store = store;
    }

    @Override
    public Promise<TableData> queryTable(TableModel tableModel) {
        try {
            return Promise.resolved(store.queryTable(AuthenticatedUser.getAnonymous(), tableModel));
        } catch(Exception e) {
            return Promise.rejected(e);
        }
    }

    @Override
    public Promise<Resource> get(ResourceId resourceId) {
        return Promise.resolved(store.get(AuthenticatedUser.getAnonymous(), resourceId));
    }

    @Override
    public Promise<List<ResourceNode>> queryRoots() {
        return null;
    }

    @Override
    public Promise<ResourceTree> queryTree(ResourceId rootId) {
        return Promise.resolved(store.queryTree(
                AuthenticatedUser.getAnonymous(), new ResourceTreeRequest(rootId)));
    }
}
