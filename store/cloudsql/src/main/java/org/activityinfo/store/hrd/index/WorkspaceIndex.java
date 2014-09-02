package org.activityinfo.store.hrd.index;

import com.google.appengine.api.datastore.*;
import com.google.common.collect.Lists;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.Resources;

import java.util.Collection;
import java.util.List;

/**
 * Index of workspaces owned, shared with, and starred by users.
 */
public class WorkspaceIndex {

    public static final String PARENT_KIND = "User";

    public static final String INDEX_KIND = "Workspace";

    public static final String OWNED_PROPERTY = "own";

    public static final String STARRED_PROPERTY = "star";


    public static boolean isWorkspace(Resource resource) {
        return resource.getOwnerId().equals(Resources.ROOT_ID);
    }

    public static Key parentKey(AuthenticatedUser user) {
        return KeyFactory.createKey(PARENT_KIND, user.getUserResourceId().asString());
    }

    public static Entity createOwnerIndex(ResourceId workspaceId, AuthenticatedUser user) {
        Entity entity = new Entity(key(workspaceId, user));
        entity.setProperty(OWNED_PROPERTY, true);
        entity.setProperty(STARRED_PROPERTY, true);
        return entity;
    }

    private static Key key(ResourceId workspaceId, AuthenticatedUser user) {
        return KeyFactory.createKey(parentKey(user), INDEX_KIND, workspaceId.asString());
    }

    public static List<ResourceNode> queryUserWorkspaces(DatastoreService datastore, AuthenticatedUser user) {
        Query query = new Query(parentKey(user)).setKeysOnly();
        List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(100));
        List<Key> workspaceKeys = Lists.newArrayList();
        for(Entity entity : entities) {
            ResourceId resourceId = ResourceId.valueOf(entity.getKey().getName());
            workspaceKeys.add(FolderIndex.folderKey(resourceId));
        }

        Collection<Entity> indices = datastore.get(workspaceKeys).values();
        List<ResourceNode> resourceNodes = Lists.newArrayList();
        for(Entity index : indices) {
            resourceNodes.add(FolderIndex.fromEntity(index));
        }
        return resourceNodes;
    }

}
