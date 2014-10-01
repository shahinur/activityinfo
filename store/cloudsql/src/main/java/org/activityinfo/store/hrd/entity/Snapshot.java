package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import java.util.Iterator;
import java.util.List;

import static com.google.appengine.api.datastore.Entity.KEY_RESERVED_PROPERTY;
import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static com.google.appengine.api.datastore.Query.CompositeFilterOperator.AND;
import static com.google.appengine.api.datastore.Query.FilterOperator.EQUAL;
import static com.google.appengine.api.datastore.Query.FilterOperator.GREATER_THAN;
import static com.google.appengine.api.datastore.Query.FilterOperator.LESS_THAN_OR_EQUAL;
import static com.google.appengine.api.datastore.Query.SortDirection.DESCENDING;
import static org.activityinfo.store.hrd.entity.Content.*;
import static org.activityinfo.store.hrd.entity.Workspace.ROOT_KIND;

/**
 * Datastore entity that stores the properties and author of a given version of a resource.
 * In other words, all versions of all resources are stored in the datastore.
 */
public class Snapshot {

    public static final String SNAPSHOT_KIND = "S";

    public static final String PARENT_KIND = "V";

    public static final String RESOURCE_ID_PROPERTY = "r";

    public static final String TIMESTAMP_PROPERTY = "t";

    public static final String USER_PROPERTY = "u";

    final private Key snapshotKey;
    final private Key workspaceKey;

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
        } else if (!ROOT_KIND.equals(snapshotKey.getParent().getParent().getKind())) {
            throw new IllegalArgumentException("Key's parent's parent is not a workspace key");
        }
        this.snapshotKey = snapshotKey;
        this.workspaceKey = snapshotKey.getParent().getParent();
    }

    /**
     * Writes the given resource as a snapshot to the datastore
     *
     */
    public void put(WorkspaceTransaction tx, Resource resource) {
        Entity entity = asEntity(resource, tx.getUser().getId());
        tx.put(entity);
    }

    private Entity asEntity(Resource resource, int userId) {
        Entity entity = new Entity(snapshotKey);
        entity.setProperty(VERSION_PROPERTY, resource.getVersion());
        entity.setProperty(RESOURCE_ID_PROPERTY, resource.getId().asString());
        entity.setUnindexedProperty(TIMESTAMP_PROPERTY, System.currentTimeMillis());
        entity.setUnindexedProperty(USER_PROPERTY, userId);
        entity.setUnindexedProperty(OWNER_PROPERTY, resource.getOwnerId().asString());

        Content.writeProperties(resource, entity);
        return entity;
    }

    public void markDeleted(WorkspaceTransaction tx) throws EntityNotFoundException {
        Entity entity = new Entity(snapshotKey);
        entity.setProperty(Content.DELETED_PROPERTY, true);
        tx.put(entity);
    }

    public Resource get(WorkspaceTransaction tx) throws EntityNotFoundException {
        return deserializeResource(tx.get(snapshotKey));
    }

    public Key getParentKey() {
        return snapshotKey.getParent();
    }

    public static Optional<Snapshot> getSnapshotAsOf(WorkspaceTransaction tx, ResourceId resourceId, long version) {
        if (version > 0) {
            List<Filter> filters = Lists.<Filter>newArrayList(
                    new FilterPredicate(VERSION_PROPERTY, LESS_THAN_OR_EQUAL, version),
                    new FilterPredicate(RESOURCE_ID_PROPERTY, EQUAL, resourceId.asString()));
            CompositeFilter compositeFilter = new CompositeFilter(AND, filters);

            Query query = new Query(SNAPSHOT_KIND)
                .setFilter(compositeFilter)
                .addSort(VERSION_PROPERTY, DESCENDING)
                .setKeysOnly();

            Iterator<Entity> iterator = tx.prepare(query).asIterator(withLimit(1));

            if (iterator.hasNext()) {
                return Optional.of(new Snapshot(iterator.next().getKey()));
            }
        }

        return Optional.absent();
    }

    public static Iterable<Snapshot> getSnapshotsAfter(WorkspaceTransaction tx, long version) {

        Key startKey = versionKey(tx.getWorkspace().getRootKey(), version+1);

        Query query = new Query(SNAPSHOT_KIND)
            .setAncestor(tx.getWorkspace().getRootKey())
            .setFilter(new FilterPredicate(KEY_RESERVED_PROPERTY, GREATER_THAN, startKey))
            .addSort(KEY_RESERVED_PROPERTY)
            .setKeysOnly();

        Iterable<Entity> iterable = tx.prepare(query).asIterable();

        return Iterables.transform(iterable, new Function<Entity, Snapshot>() {
            public Snapshot apply(Entity entity) {
                return new Snapshot(entity.getKey());
            }
        });
    }
}
