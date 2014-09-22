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
                cacheFolder(save, result);
            }

            if(isFolderItem(save.getUpdatedResource())) {
                ResourceId ownerId = save.getUpdatedResource().getOwnerId();
                Status<FolderProjection> folder = get(ownerId);
                if(folder.isAvailable()) {
                    // update folder root
                    folder.get().getRootNode().setLabel(save.getUpdatedResource().getString(FolderClass.LABEL_FIELD_ID.asString()));

                    // update childs
                    updateChildren(folder.get(), save.getUpdatedResource());
                }
            }
        }
    }

    private boolean isFolderItem(Resource updatedResource) {
        return updatedResource.getString("classId").startsWith("_");
    }

    private void updateChildren(FolderProjection folder, Resource updatedResource) {
        List<ResourceNode> children = folder.getRootNode().getChildren();
        int index = getItemIndexById(children, updatedResource.getId());
        if(index >= 0) {
            children.set(index, new ResourceNode(updatedResource));
        } else {
            children.add(new ResourceNode(updatedResource));
        }
        fireChange();
    }

    private int getItemIndexById(List<ResourceNode> items, ResourceId id) {
        for(int i=0;i!=items.size();++i) {
            if(items.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void cacheFolder(SaveRequest request, UpdateResult result) {
        Resource resource = request.getUpdatedResource();

        if(request.isNewResource()) { // New Folder, so no children yet
            ResourceNode node = new ResourceNode(resource);
            node.setVersion(result.getNewVersion());
            folders.put(node.getId(), Status.cache(new FolderProjection(node)));

        } else {
            if (folders.containsKey(resource.getId())) {
                FolderProjection folder = folders.get(resource.getId()).get();
                folder.getRootNode().setLabel(resource.getString(FolderClass.LABEL_FIELD_ID.asString()));
            }
        }
        fireChange();
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
