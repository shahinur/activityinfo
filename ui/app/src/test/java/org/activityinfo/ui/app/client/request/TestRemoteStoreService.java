package org.activityinfo.ui.app.client.request;

import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.FolderRequest;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tasks.UserTask;

import java.util.List;
import java.util.Set;

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
    public Promise<UserResource> getUserResource(ResourceId resourceId) {
        return Promise.resolved(UserResource.userResource(store.get(user, resourceId)));
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
    public Promise<UserTask> startImport(ResourceId ownerId, BlobId blobId) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<List<UserTask>> getTasks() {
        return Promise.rejected(new UnsupportedOperationException());
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
    public Promise<List<Bucket>> queryCube(PivotTableModel cubeModel) {
        return Promise.resolved(store.queryCube(user, cubeModel));
    }

    @Override
    public Promise<FolderProjection> getFolder(ResourceId rootId) {
        return Promise.resolved(store.queryTree(user, new FolderRequest(rootId)));
    }

    @Override
    public Promise<Void> remove(Set<ResourceId> resources) {
        return Promise.rejected(new UnsupportedOperationException());
    }


}
