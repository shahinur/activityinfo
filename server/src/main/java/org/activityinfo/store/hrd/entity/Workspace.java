package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.store.hrd.index.AcrIndex;
import org.activityinfo.store.hrd.index.WorkspaceIndex;

import java.util.List;

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

        long newVersion = createResource(tx, resource, Optional.<Long>absent());

        AccessControlRule acr = new AccessControlRule(resource.getId(), tx.getUser().getUserResourceId());
        acr.setOwner(true);
        getAcrIndex().put(tx, acr);

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
    public long createResource(WorkspaceTransaction tx, Resource resource, Optional<Long> rowIndex) {
        Preconditions.checkNotNull(tx);
        Preconditions.checkNotNull(resource.getOwnerId());

        resource = resource.copy();
        resource.setVersion(tx.incrementVersion());

        getLatestContent(resource.getId()).create(tx, resource, rowIndex);
        getSnapshot(resource.getId(), resource.getVersion()).put(tx, resource);

        return resource.getVersion();
    }


    public long createResource(WorkspaceTransaction tx, Resource resource) {
        return createResource(tx, resource, Optional.<Long>absent());
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

    public FormMetadata getFormMetadata(ResourceId formClassId) {
        return new FormMetadata(rootKey, formClassId);
    }

    public AcrIndex getAcrIndex() {
        return new AcrIndex(rootKey);
    }

    public long deleteResourceTree(WorkspaceTransaction tx, ResourceId resourceId) throws EntityNotFoundException {
        Resource resource = getLatestContent(resourceId).get(tx);
        resource.setDeleted(true);

        long newVersion = updateResource(tx, resource);

        // mark descendants as deleted too
        deleteChilds(tx, resourceId, resourceId);
        tx.commit();
        return newVersion;
    }

    private void deleteChilds(WorkspaceTransaction tx, ResourceId resourceId, ResourceId parentId) throws EntityNotFoundException {
        Query descendantsQuery = new Query(LatestContent.KIND)
                .setAncestor(getRootKey())
                .setFilter(Query.CompositeFilterOperator.and(
                        new Query.FilterPredicate(Content.OWNER_PROPERTY, Query.FilterOperator.EQUAL, parentId.asString()),
                        new Query.FilterPredicate(Content.DELETED_PROPERTY, Query.FilterOperator.EQUAL, false)
                ))
                .setKeysOnly();

        List<Entity> descendants = Lists.newArrayList(tx.prepare(descendantsQuery).asIterator());
        for (Entity entity : descendants) {
            ResourceId id = ResourceId.valueOf(entity.getKey().getName());

            Resource childResource = getLatestContent(id).get(tx);
            childResource.setDeleted(true);
            updateResource(tx, childResource);
            deleteChilds(tx, resourceId, id); // recursive deletion
        }
    }
}
