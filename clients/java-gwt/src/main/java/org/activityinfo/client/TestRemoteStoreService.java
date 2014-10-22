package org.activityinfo.client;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.ColumnSet;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.FolderRequest;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.StoreReader;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tasks.UserTask;

import java.util.List;

public class TestRemoteStoreService implements ActivityInfoAsyncClient {

    private final ResourceStore store;
    private Supplier<AuthenticatedUser> currentUser = Suppliers.ofInstance(AuthenticatedUser.getAnonymous());

    public TestRemoteStoreService(ResourceStore store) {
        this.store = store;
    }

    public TestRemoteStoreService(ResourceStore store, Supplier<AuthenticatedUser> user) {
        this(store);
        this.currentUser = user;
    }

    @Override
    public Promise<ColumnSet> queryColumns(TableModel tableModel) {
        try {
            try(StoreReader reader = store.openReader(currentUser.get())) {
                return Promise.resolved(reader.queryColumns(tableModel));
            }
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
        return Promise.resolved(store.get(currentUser.get(), resourceId));
    }

    @Override
    public Promise<UpdateResult> put(Resource resource) {
        return Promise.resolved(store.put(currentUser.get(), resource));
    }

    @Override
    public String getBlobDownloadUrl(BlobId blobId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<UpdateResult> create(Resource resource) {
        return Promise.resolved(store.create(currentUser.get(), resource));
    }

    @Override
    public Promise<List<UserTask>> getTasks() {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<UserTask> startTask(Record taskModel) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<UserTask> executeTask(Record taskModel) {
        return startTask(taskModel);
    }

    @Override
    public Promise<List<ResourceNode>> getWorkspaces() {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<FolderProjection> getFolder(ResourceId rootId) {
        return Promise.resolved(store.queryTree(
                currentUser.get(), new FolderRequest(rootId)));
    }

    @Override
    public Promise<UpdateResult> remove(ResourceId resourceId) {
        return Promise.resolved(store.delete(currentUser.get(), resourceId));
    }

    @Override
    public Promise<Void> ping() {
        return Promise.done();
    }
}
