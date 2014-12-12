package org.activityinfo.service.store;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.UserResource;

import java.util.List;

public interface ResourceStore {

    /**
     * Fetches the latest version of the resource from the store.
     */
    UserResource get(AuthenticatedUser user, ResourceId resourceId);

    List<Resource> getAccessControlRules(AuthenticatedUser user, ResourceId resourceId);


    /**
     * Deletes {@code Resource}s from the store
     *
     * @param user      authenticated user
     * @param resourceId resource id
     * @return result whether resource was deleted or not
     */
    UpdateResult delete(AuthenticatedUser user, ResourceId resourceId);

    /**
     * Creates or updates a {@code Resource} within the store.
     *
     * <p>Non Jax-rs convenience method
     *
     * @param user
     * @param resource
     * @return
     */
    UpdateResult put(AuthenticatedUser user, Resource resource);

    /**
     * Creates a {@code Resource} within the store.
     *
     * @param user
     * @param resource
     * @return
     */
    UpdateResult create(AuthenticatedUser user, Resource resource);

    /**
     * Fetches an outline of Resources, returning only their id and label.
     */
    FolderProjection queryTree(AuthenticatedUser user, FolderRequest request);


    /**
     *
     * @return a list of workspaces owned or explicitly shared with a
     * given user
     */
    List<ResourceNode> getOwnedOrSharedWorkspaces(AuthenticatedUser user);

    List<Resource> getUpdates(AuthenticatedUser user, ResourceId workspaceId, long version);

    StoreLoader beginLoad(AuthenticatedUser user, ResourceId parentId);

}
