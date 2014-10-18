package org.activityinfo.ui.app.client.request;

import org.activityinfo.client.ActivityInfoAsyncClient;
import org.activityinfo.client.StatusCodeException;
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
import org.activityinfo.service.tasks.UserTaskStatus;

import java.util.List;

public class TestRemoteStoreService implements ActivityInfoAsyncClient {

    private ResourceStore store;
    private AuthenticatedUser user;

    public boolean fail = false;

    public TestRemoteStoreService(ResourceStore store) {
        this.store = store;
    }

    @Override
    public Promise<UserResource> get(ResourceId resourceId) {
        return Promise.resolved(store.get(user, resourceId));
    }

    @Override
    public Promise<UpdateResult> put(Resource resource) {
        return Promise.resolved(store.put(user, resource));
    }

    @Override
    public String getBlobDownloadUrl(BlobId blobId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<UpdateResult> create(Resource resource) {
        return Promise.resolved(store.create(user, resource));
    }

    @Override
    public Promise<List<UserTask>> getTasks() {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<UserTask> startTask(Record taskModel) {
        if(fail) {
            return Promise.rejected(new StatusCodeException(400));
        } else {
            UserTask task = new UserTask();
            task.setId(Resources.generateId().asString());
            task.setTimeStarted(System.currentTimeMillis());
            task.setTaskModel(taskModel);
            task.setStatus(UserTaskStatus.RUNNING);
            return Promise.resolved(task);
        }
    }

    @Override
    public Promise<UserTask> executeTask(Record taskModel) {
        return startTask(taskModel);
    }

    @Override
    public Promise<List<ResourceNode>> getWorkspaces() {
        return Promise.resolved(store.getOwnedOrSharedWorkspaces(AuthenticatedUser.getAnonymous()));
    }

    @Override
    public Promise<ColumnSet> queryColumns(TableModel tableModel) {
        try(StoreReader reader = store.openReader(user)) {
            return Promise.resolved(reader.queryColumns(tableModel));
        }
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
    public Promise<UpdateResult> remove(ResourceId resourceId) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<Void> ping() {
        return Promise.done();
    }



}
