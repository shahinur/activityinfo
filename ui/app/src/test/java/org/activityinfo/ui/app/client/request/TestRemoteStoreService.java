package org.activityinfo.ui.app.client.request;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.FolderRequest;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.UpdateResult;

import java.util.List;

public class TestRemoteStoreService implements RemoteStoreService {

    private ResourceStore store;
    private AuthenticatedUser user;

    public TestRemoteStoreService(ResourceStore store) {
        this.store = store;
    }

    @Override
    public Promise<Resource> get(ResourceId resourceId) {
        return Promise.resolved(store.get(user, resourceId));
    }

    @Override
    public Promise<UpdateResult> put(Resource resource) {
        return Promise.resolved(store.put(user, resource));
    }

    @Override
    public Promise<UpdateResult> create(Resource resource) {
        return Promise.resolved(store.create(user, resource));
    }

    @Override
    public Promise<List<ResourceNode>> getWorkspaces() {
        return Promise.resolved(store.getOwnedOrSharedWorkspaces(AuthenticatedUser.getAnonymous()));
    }

    @Override
    public Promise<TableData> queryTable(TableModel tableModel) {
        return Promise.resolved(store.queryTable(user, tableModel));
    }

    @Override
    public Promise<FolderProjection> getFolder(ResourceId rootId) {
        return Promise.resolved(store.queryTree(user, new FolderRequest(rootId)));
    }

}
