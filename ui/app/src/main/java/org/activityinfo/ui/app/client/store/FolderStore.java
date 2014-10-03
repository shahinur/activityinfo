package org.activityinfo.ui.app.client.store;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.service.store.CommitStatus;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.ui.app.client.action.RemoteUpdateHandler;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.folder.FolderPlace;
import org.activityinfo.ui.app.client.request.FetchFolder;
import org.activityinfo.ui.app.client.request.RemoveRequest;
import org.activityinfo.ui.app.client.request.Request;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;
import org.activityinfo.ui.flux.store.Status;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FolderStore extends AbstractStore implements RemoteUpdateHandler {

    private Map<ResourceId, Status<FolderProjection>> folders = Maps.newHashMap();

    private Set<ResourceId> loading = Sets.newHashSet();
    private final Router router;

    public FolderStore(Dispatcher dispatcher, Router router) {
        super(dispatcher);
        this.router = router;
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
            Resource updatedResource = save.getUpdatedResource();

            if(updatedResource.getValue().getClassId().equals(FolderClass.CLASS_ID)) {
                cacheFolder(save, result);
            }

            if (isFolderItem(updatedResource)) {
                Status<FolderProjection> folder = get(updatedResource.getOwnerId());
                if (folder.isAvailable()) {

                    updateChildren(folder.get(), updatedResource, true);
                }
            }
        } else if (request instanceof RemoveRequest) {
            UpdateResult updatedResult = (UpdateResult) response;

            if (updatedResult.getStatus() == CommitStatus.COMMITTED) {
                if (!loading.remove(updatedResult.getResourceId())) {
                    folders.remove(updatedResult.getResourceId());

                    Place currentPlace = router.getCurrentPlace();
                    if (currentPlace instanceof FolderPlace) {
                        FolderPlace folderPlace = (FolderPlace) currentPlace;
                        Status<FolderProjection> folderProjection = folders.get(folderPlace.getResourceId());
                        if (folderProjection != null && folderProjection.isAvailable()) {
                            removeDescendant(folderProjection.get().getRootNode(), updatedResult.getResourceId());
                        }
                    }
                    fireChange();
                }
            }
        }
    }

    private static boolean removeDescendant(ResourceNode node, ResourceId id) {
        Iterator<ResourceNode> iterator = node.getChildren().iterator();
        while(iterator.hasNext()) {
            ResourceNode next = iterator.next();
            if (next.getId().equals(id)) {
                iterator.remove();
                return true;
            } else {
                removeDescendant(next, id);
            }
        }
        return false;
    }

    private boolean isFolderItem(Resource updatedResource) {
        return updatedResource.getValue().getClassId().isApplicationDefined();
    }

    private void updateChildren(FolderProjection folder, final Resource updatedResource, boolean editAllowed) {
        List<ResourceNode> children = folder.getRootNode().getChildren();
        int index = Iterables.indexOf(children, new Predicate<ResourceNode>() {
            @Override
            public boolean apply(ResourceNode input) {
                return input.getId().equals(updatedResource.getId());
            }
        });
        ResourceNode node = new ResourceNode(updatedResource)
                .setEditAllowed(editAllowed);
        if (index >= 0) {
            children.set(index, node);
        } else {
            children.add(node);
        }
        fireChange();
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
                folder.getRootNode().setLabel(resource.getValue().getString(FolderClass.LABEL_FIELD_ID.asString()));
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
