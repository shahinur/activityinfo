package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import static com.google.appengine.api.datastore.Query.FilterOperator.GREATER_THAN;
import static org.activityinfo.store.hrd.entity.Content.VERSION_PROPERTY;
import static org.activityinfo.store.hrd.entity.Content.deserializeResource;

/**
 * Datastore entity that stores the properties and author of a given version of a resource.
 * In other words, all versions of all resources are stored in the datastore.
 */
public class Snapshot {

    public static final String SNAPSHOT_KIND = "S";

    public static final String PARENT_KIND = "V";

    public static final String TIMESTAMP_PROPERTY = "t";

    public static final String USER_PROPERTY = "u";

    final private Key snapshotKey;
    private Key workspaceKey;

    public Snapshot(Key workspaceKey, ResourceId id, long version) {
        // Compose the key for the snapshot entity as
        // WORKSPACE -> VERSION -> RESOURCE_ID
        // So that we can simply sort by key to get a list of updates
        this.workspaceKey = workspaceKey;
        Key versionKey = versionKey(this.workspaceKey, version);
        this.snapshotKey = KeyFactory.createKey(versionKey, SNAPSHOT_KIND, id.asString());
    }

    public ResourceId getResourceId() {
        return ResourceId.valueOf(snapshotKey.getName());
    }

    private static Key versionKey(Key workspaceKey, long version) {
        return KeyFactory.createKey(workspaceKey, PARENT_KIND, version);
    }

    private Snapshot(Key snapshotKey) {
        if (!SNAPSHOT_KIND.equals(snapshotKey.getKind())) {
            throw new IllegalArgumentException("Key is not a snapshot key");
        } else if (!PARENT_KIND.equals(snapshotKey.getParent().getKind())) {
            throw new IllegalArgumentException("Key's parent is not a snapshot parent key");
        }
        this.snapshotKey = snapshotKey;
    }

    /**
     * Writes the given resource as a snapshot to the datastore
     *
     */
    public void put(WorkspaceTransaction tx, Resource resource) {
        Entity entity = new Entity(snapshotKey);
        entity.setProperty(VERSION_PROPERTY, resource.getVersion());
        entity.setUnindexedProperty(TIMESTAMP_PROPERTY, System.currentTimeMillis());
        entity.setUnindexedProperty(USER_PROPERTY, tx.getUser().getId());

        Content.writeProperties(resource, entity);

        tx.put(entity);
    }

    public Resource get(WorkspaceTransaction tx) throws EntityNotFoundException {
        return deserializeResource(tx.get(snapshotKey));
    }

    public Key getParentKey() {
        return snapshotKey.getParent();
    }

    public static Iterable<Snapshot> getSnapshotsAfter(WorkspaceTransaction tx, long version) {

        Key startKey = versionKey(tx.getWorkspace().getRootKey(), version+1);

        Query query = new Query(SNAPSHOT_KIND)
            .setAncestor(tx.getWorkspace().getRootKey())
            .setFilter(new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, GREATER_THAN, startKey))
            .addSort(Entity.KEY_RESERVED_PROPERTY)
            .setKeysOnly();

        Iterable<Entity> iterable = tx.prepare(query).asIterable();

        return Iterables.transform(iterable, new Function<Entity, Snapshot>() {
            public Snapshot apply(Entity entity) {
                return new Snapshot(entity.getKey());
            }
        });
    }

}
