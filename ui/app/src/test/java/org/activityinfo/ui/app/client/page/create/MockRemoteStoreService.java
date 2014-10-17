package org.activityinfo.ui.app.client.page.create;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.client.ActivityInfoAsyncClient;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tasks.UserTask;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class MockRemoteStoreService implements ActivityInfoAsyncClient {

    private List<Resource> createdResources = Lists.newArrayList();

    private Map<ResourceId, Promise<FolderProjection>> folders = Maps.newHashMap();

    private Promise<?> lastPromise;

    @Override
    public Promise<UserResource> get(ResourceId resourceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<UpdateResult> put(Resource resource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getBlobDownloadUrl(BlobId blobId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<UpdateResult> create(Resource resource) {
        createdResources.add(resource);
        return Promise.resolved(UpdateResult.committed(resource.getId(), 1));
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
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<TableData> queryTable(TableModel tableModel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<List<Bucket>> queryCube(PivotTableModel cubeModel) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<FolderProjection> getFolder(ResourceId rootId) {
        Promise<FolderProjection> promise = new Promise<FolderProjection>();
        folders.put(rootId, promise);
        return promise;
    }

    @Override
    public Promise<UpdateResult> remove(ResourceId resourceId) {
        folders.remove(resourceId);

        Set<Resource> toRemove = Sets.newHashSet();
        for (Resource resource : createdResources) {
            if (resourceId.equals(resource.getId())) {
                toRemove.add(resource);
            }
        }
        createdResources.removeAll(toRemove);

        return Promise.resolved(UpdateResult.committed(resourceId, 1));
    }

    @Override
    public Promise<Void> ping() {
        return Promise.done();
    }

    public Resource getOnlyCreatedResource() {
        assertThat(createdResources, hasSize(1));

        return createdResources.get(0);
    }

    public void resolveFolderRequest(FolderProjection tree) {
        assert folders.containsKey(tree.getRootNode().getId()) : "No request for " + tree.getRootNode().getId();
        folders.get(tree.getRootNode().getId()).resolve(tree);
    }


}
