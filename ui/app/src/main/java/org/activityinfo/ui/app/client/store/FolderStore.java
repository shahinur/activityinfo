package org.activityinfo.ui.app.client.store;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.ui.app.client.action.RemoteUpdateHandler;
import org.activityinfo.ui.app.client.request.FetchFolder;
import org.activityinfo.ui.app.client.request.Request;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;
import org.activityinfo.ui.flux.store.Status;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FolderStore extends AbstractStore implements RemoteUpdateHandler {

    private Map<ResourceId, Status<FolderProjection>> folders = Maps.newHashMap();

    private Set<ResourceId> loading = Sets.newHashSet();

    public FolderStore(Dispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void requestStarted(Request request) {
        loading.add(folderId(request));
    }

    @Override
    public void requestFailed(Request request, Exception e) {
        loading.remove(folderId(request));
    }

    private ResourceId folderId(Request request) {
        if(request instanceof FetchFolder) {
            return ((FetchFolder) request).getFolderId();
        } else {
            return null;
        }
    }

    @Override
    public <R> void processUpdate(Request<R> request, R response) {
        if(response instanceof FolderProjection) {
            cache((FolderProjection)response);
        } else if(request instanceof SaveRequest) {
            SaveRequest save = (SaveRequest) request;
            UpdateResult result = (UpdateResult) response;

            if(save.getUpdatedResource().getString("classId").equals(FolderClass.CLASS_ID.asString())) {
                cacheNewFolder(save, result);
            }
        }
    }

    private void cacheNewFolder(SaveRequest request, UpdateResult result) {
        if(request.isNewResource()) {
            // New Folder, so no children yet
            Resource resource = request.getUpdatedResource();
            ResourceNode node = new ResourceNode(resource);
            node.setVersion(result.getNewVersion());
            folders.put(node.getId(), Status.cache(new FolderProjection(node)));
            fireChange();
        }
    }

    private <R> void cache(FolderProjection response) {
        folders.put(response.getRootNode().getId(), Status.cache(response));
        loading.remove(response.getRootNode().getId());
        fireChange();
    }

    public Status<List<ResourceNode>> getFolderItems(ResourceId folderId) {
        return get(folderId).join(new Function<FolderProjection, List<ResourceNode>>() {
            @Override
            public List<ResourceNode> apply(FolderProjection input) {
                return input.getRootNode().getChildren();
            }
        });
    }

    public Status<FolderProjection> get(ResourceId resourceId) {
        Status<FolderProjection> status = folders.get(resourceId);
        if(status == null) {
            return status.unavailable();
        } else {
            return status.withLoading(loading.contains(resourceId));
        }
    }
}
