package org.activityinfo.store.test;

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
    public Promise<List<Bucket>> queryCube(PivotTableModel cubeModel) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<UserResource> get(ResourceId resourceId) {
        return Promise.resolved(store.get(AuthenticatedUser.getAnonymous(), resourceId));
    }

    @Override
    public Promise<UpdateResult> put(Resource resource) {
        return Promise.resolved(store.put(AuthenticatedUser.getAnonymous(), resource));
    }

    @Override
    public Promise<UpdateResult> create(Resource resource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<UserTask> startImport(ResourceId ownerId, BlobId blobId) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<List<UserTask>> getTasks() {
        return null;
    }

    @Override
    public Promise<List<ResourceNode>> getWorkspaces() {
        return null;
    }

    @Override
    public Promise<FolderProjection> getFolder(ResourceId rootId) {
        return Promise.resolved(store.queryTree(
            AuthenticatedUser.getAnonymous(), new FolderRequest(rootId)));
    }

    @Override
    public Promise<Set<UpdateResult>> remove(Set<ResourceId> resources) {
        throw new UnsupportedOperationException();
    }
}
