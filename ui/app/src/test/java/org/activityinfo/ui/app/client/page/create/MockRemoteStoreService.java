package org.activityinfo.ui.app.client.page.create;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.service.store.UpdateResult;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class MockRemoteStoreService implements RemoteStoreService {

    private List<Resource> createdResources = Lists.newArrayList();

    private Map<ResourceId, Promise<FolderProjection>> folders = Maps.newHashMap();

    private Promise<?> lastPromise;

    @Override
    public Promise<Resource> get(ResourceId resourceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<UpdateResult> put(Resource resource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<UpdateResult> create(Resource resource) {
        createdResources.add(resource);
        return Promise.resolved(UpdateResult.committed(resource.getId(), 1));
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
    public Promise<FolderProjection> getFolder(ResourceId rootId) {
        Promise<FolderProjection> promise = new Promise<FolderProjection>();
        folders.put(rootId, promise);
        return promise;
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
