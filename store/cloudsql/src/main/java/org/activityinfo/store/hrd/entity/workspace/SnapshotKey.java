package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Preconditions;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.hrd.tx.IsKey;

/**
 * Datastore key for Snapshot entities.
 *
 */
public class SnapshotKey implements IsKey<Snapshot> {

    public static final String KIND = "S";

    private final Key key;

    public SnapshotKey(Key key) {
        checkKey(key);
        this.key = key;
    }

    public SnapshotKey(VersionKey versionKey, ResourceId resourceId) {
        // Compose the key for the snapshot entity as
        // WORKSPACE -> VERSION -> RESOURCE_ID
        // So that we can simply sort by key to get a list of updates
        this.key = KeyFactory.createKey(versionKey.unwrap(), KIND, resourceId.asString());
    }

    public static void checkKey(Key key) {
        Preconditions.checkArgument(key.getKind().equals(KIND), "Expected key of kind %s, found %s", key);
        VersionKey.checkKey(key.getParent());
    }

    public ResourceId getResourceId() {
        return ResourceId.valueOf(key.getName());
    }

    public VersionKey getParent() {
        return new VersionKey(key.getParent());
    }

    @Override
    public Key unwrap() {
        return key;
    }

    @Override
    public Snapshot wrapEntity(Entity entity) {
        return new Snapshot(this, entity);
    }


}
