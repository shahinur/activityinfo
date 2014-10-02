package org.activityinfo.ui.app.client.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.service.store.CommitStatus;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.ui.app.client.action.RemoteUpdateHandler;
import org.activityinfo.ui.app.client.request.FetchWorkspaces;
import org.activityinfo.ui.app.client.request.RemoveRequest;
import org.activityinfo.ui.app.client.request.Request;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;

import java.util.List;
import java.util.Map;

public class WorkspaceStore extends AbstractStore implements RemoteUpdateHandler {


    /**
     * The time in millis since we had a complete refresh of the list
     */
    private double cacheTime = -1;


    private Map<ResourceId, ResourceNode> workspaces = Maps.newHashMap();

    private boolean loading = false;


    public WorkspaceStore(Dispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void requestStarted(Request request) {
        if(request instanceof FetchWorkspaces) {
            loading = true;
        }
    }

    @Override
    public void requestFailed(Request request, Exception e) {
        if(request instanceof FetchWorkspaces) {
            loading = false;
        }
    }

    @Override
    public <R> void processUpdate(Request<R> request, R response) {
        if(request instanceof FetchWorkspaces) {
            cacheWorkspaceList((List<ResourceNode>) response);

        } else if(request instanceof FolderProjection) {
            cacheFolder((FolderProjection) request);

        } else if(request instanceof SaveRequest) {
            Resource resource = ((SaveRequest) request).getUpdatedResource();
            if(resource.getOwnerId().equals(Resources.ROOT_ID)) {
                addNewWorkspace(resource, (UpdateResult)response);
            }
        } else if (request instanceof RemoveRequest) {
            UpdateResult updatedResult = (UpdateResult) response;

            if (updatedResult.getStatus() == CommitStatus.COMMITTED) {
                boolean removed = workspaces.remove(updatedResult.getResourceId()) != null;
                if (removed) {
                    fireChange();
                }
            }
        }
    }

    private void addNewWorkspace(Resource resource, UpdateResult response) {
        ResourceNode node = new ResourceNode(resource);
        node.setVersion(response.getNewVersion());
        workspaces.put(resource.getId(), node);
        fireChange();
    }

    private void cacheWorkspaceList(List<ResourceNode> nodes) {
        this.workspaces.clear();
        for(ResourceNode workspace : nodes) {
            this.workspaces.put(workspace.getId(), workspace);
        }
        cacheTime = System.currentTimeMillis();
        fireChange();
    }

    private void cacheFolder(FolderProjection folder) {
        ResourceNode folderNode = folder.getRootNode();
        if(workspaces.containsKey(folderNode.getOwnerId())) {
            if(folderNode.getOwnerId().equals(Resources.ROOT_ID)) {
                workspaces.put(folderNode.getId(), folderNode);
            } else {
                workspaces.remove(folderNode.getId());
            }
            fireChange();
        }
    }

    public List<ResourceNode> get() {
        return Lists.newArrayList(workspaces.values());
    }
}
