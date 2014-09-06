package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.*;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.hrd.index.FolderIndex;

/**
 * An entity that serves as the root of the content and revision history of
 * a single resource. Keeping them together in a single entity group ensures that
 * updates to a resource can be serialized.
 */
public class ResourceGroup {

    public static final String KIND = "G";

    /**
     * The root key of the group
     */
    private final Key rootKey;

    public ResourceGroup(ResourceId id) {
        this.rootKey = KeyFactory.createKey(KIND, id.asString());
    }

    public Key getKey() {
        return rootKey;
    }

    public LatestContent getLatestContent(ResourceId id) {
        return new LatestContent(rootKey, id);
    }

    public Snapshot getSnapshot(ResourceId resourceId, long version) {
        return new Snapshot(rootKey, resourceId, version);
    }

    public void update(DatastoreService datastore, Transaction tx, AuthenticatedUser user, Resource resource) {
        Entity contentEntity = getLatestContent(resource.getId()).createEntity(resource);
        Entity snapshot = getSnapshot(resource.getId(), resource.getVersion()).createEntity(user, resource);

        System.out.println(contentEntity.getKey());

        datastore.put(tx, contentEntity);
        datastore.put(tx, snapshot);

        if(FolderIndex.isFolderItem(resource)) {
            datastore.put(tx, FolderIndex.createEntities(resource));
        }
    }

    public boolean exists(DatastoreService datastore, ResourceId resourceId) {
        if(resourceId.asString().startsWith("_")) {
            return false;
        }
        Key key = getLatestContent(resourceId).getKey();
        System.out.println("looking for " + key);
        try {
            datastore.get(key);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

}
