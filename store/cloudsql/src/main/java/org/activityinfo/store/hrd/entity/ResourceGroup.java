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

    public static final String KIND = "R";

    private ResourceId resourceId;

    /**
     * The root key of the group
     */
    private final Key rootKey;

    public ResourceGroup(ResourceId id) {
        this.resourceId = id;
        this.rootKey = KeyFactory.createKey(KIND, id.asString());
    }

    public Key getKey() {
        return rootKey;
    }

    public LatestContent getLatestContent() {
        return new LatestContent(this);
    }

    public LatestVersion getLatestVersion() {
        return new LatestVersion(this);
    }

    public Snapshot getSnapshot(long version) {
        return new Snapshot(this, version);
    }

    public ResourceId getResourceId() {
        return resourceId;
    }

    public void update(DatastoreService datastore, Transaction tx, AuthenticatedUser user, Resource resource) {
        datastore.put(tx, getLatestContent().createEntity(resource));
        datastore.put(tx, getLatestVersion().createEntity(resource.getVersion()));
        datastore.put(tx, getSnapshot(resource.getVersion()).createEntity(user, resource));

        if(FolderIndex.isFolderItem(resource)) {
            datastore.put(tx, FolderIndex.createEntities(resource));
        }
    }

}
