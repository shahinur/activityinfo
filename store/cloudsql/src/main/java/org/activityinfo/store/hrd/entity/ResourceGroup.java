package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
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
        datastore.put(tx, getLatestContent(resource.getId()).createEntity(resource));
        datastore.put(tx, getSnapshot(resource.getId(), resource.getVersion()).createEntity(user, resource));

        if(FolderIndex.isFolderItem(resource)) {
            datastore.put(tx, FolderIndex.createEntities(resource));
        }
    }

}
