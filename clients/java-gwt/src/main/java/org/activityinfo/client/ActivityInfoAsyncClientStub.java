package org.activityinfo.client;

import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.FolderRequest;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.StoreReader;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tasks.UserTask;

import java.util.List;

public class ActivityInfoAsyncClientStub implements ActivityInfoAsyncClient {

    private final ResourceStore store;

    public ActivityInfoAsyncClientStub(ResourceStore store) {
        this.store = store;
    }

    @Override
    public Promise<TableData> queryTable(TableModel tableModel) {
        try {
            try(StoreReader reader = store.openReader(AuthenticatedUser.getAnonymous())) {
                return Promise.resolved(reader.getTable(tableModel));
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
        return Promise.resolved(store.get(AuthenticatedUser.getAnonymous(), resourceId));
    }

    @Override
    public Promise<UpdateResult> put(Resource resource) {
        return Promise.resolved(store.put(AuthenticatedUser.getAnonymous(), resource));
    }

    @Override
    public String getBlobDownloadUrl(BlobId blobId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<UpdateResult> create(Resource resource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<List<UserTask>> getTasks() {
        return null;
    }

    @Override
    public Promise<UserTask> startTask(String taskId, Record taskModel) {
        return Promise.rejected(new UnsupportedOperationException());
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
    public Promise<UpdateResult> remove(ResourceId resourceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<Void> ping() {
        return Promise.done();
    }
}
