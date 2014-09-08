package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.search.checkers.Preconditions;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.store.hrd.index.WorkspaceIndex;

/**
 * Workspaces form an Entity Group within all transactions occur with "serialized" consistency and
 * in which we maintain a monotonically increasing version number that can be used for synchronization.
 */
public class Workspace {

    public static final String ROOT_KIND = "W";

    private ResourceId workspaceId;


    /**
     * The root key of the group
     */
    private final Key rootKey;

    private FolderIndex folderIndex;

    /**
     *
     * @param workspaceId the resourceId of the workspace
     */
    public Workspace(ResourceId workspaceId) {
        this.workspaceId = workspaceId;
        this.rootKey = KeyFactory.createKey(ROOT_KIND, workspaceId.asString());
    }


    public ResourceId getWorkspaceId() {
        return workspaceId;
    }

    /**
     *
     * @return the root key for the Workspace entity group.
     */
    public Key getRootKey() {
        return rootKey;
    }

    /**
     *
     * @return the entity which stores the current workspace version
     */
    public WorkspaceVersion getVersion() {
        return new WorkspaceVersion(rootKey);
    }

    /**
     *
     * @return the entity holding the latest content (properties) of the resource identified by
     * {@code id}
     */
    public LatestContent getLatestContent(ResourceId id) {
        return new LatestContent(rootKey, id);
    }


    public Snapshot getSnapshot(ResourceId resourceId, long version) {
        return new Snapshot(rootKey, resourceId, version);
    }

    public FolderIndex getFolderIndex() {
        if(folderIndex == null) {
            folderIndex = new FolderIndex(rootKey);
        }
        return folderIndex;
    }

    public long createWorkspace(WorkspaceTransaction tx, Resource resource) {

        Preconditions.checkArgument(resource.getOwnerId().equals(Resources.ROOT_ID),
            "workspace owner must be root");
        Preconditions.checkArgument(resource.getId().equals(workspaceId),
            "resource id does not match workspace id");

        long newVersion = createResource(tx, resource);

        AccessControlRule acr = new AccessControlRule(resource.getId(), tx.getUser().getUserResourceId());
        acr.setOwner(true);
        Resource acrResource = acr.asResource();
        acrResource.setVersion(newVersion);
        createResource(tx, acrResource);

        // add to the index
        WorkspaceIndex.addOwnerIndexEntry(tx, this);

        return newVersion;
    }


    /**
     * Writes the updated {@code resource} to the datastore.
     * @param tx the transaction in which to apply the changes.
     * @param resource the updated version
     * @return the new version of the resource
     */
    public long createResource(WorkspaceTransaction tx, Resource resource) {
        resource = resource.copy();
        resource.setVersion(tx.incrementVersion());

        getLatestContent(resource.getId()).create(tx, resource);
        getSnapshot(resource.getId(), resource.getVersion()).put(tx, resource);

        return resource.getVersion();
    }

    /**
     * Writes the updated {@code resource} to the datastore.
     * @param tx the transaction in which to apply the changes.
     * @param resource the updated version
     * @return the new version of the resource
     */
    public long updateResource(WorkspaceTransaction tx, Resource resource) {
        resource = resource.copy();
        resource.setVersion(tx.incrementVersion());

        getLatestContent(resource.getId()).update(tx, resource);
        getSnapshot(resource.getId(), resource.getVersion()).put(tx, resource);

        return resource.getVersion();
    }
}
