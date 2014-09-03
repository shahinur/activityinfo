package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import static com.google.appengine.api.datastore.Query.FilterOperator.GREATER_THAN;
import static com.google.appengine.api.datastore.Query.SortDirection.ASCENDING;
import static org.activityinfo.store.hrd.entity.Content.VERSION_PROPERTY;
import static org.activityinfo.store.hrd.entity.Content.deserializeResource;

/**
 * An entity in the Resource entity group that stores the properties
 * and author of a given version of a resource.
 */
public class Snapshot {

    public static final String KIND = "S";

    public static final String PARENT_KIND = "R";

    public static final String TIMESTAMP_PROPERTY = "t";

    public static final String USER_PROPERTY = "u";

    final private Key key;

    public Snapshot(Key rootKey, ResourceId id, long version) {
        Key parentKind = KeyFactory.createKey(rootKey, PARENT_KIND, id.asString());
        this.key = KeyFactory.createKey(parentKind, KIND, version);
    }

    private Snapshot(Key key) {
        if (!KIND.equals(key.getKind())) {
            throw new IllegalArgumentException("Key is not a snapshot key");
        } else if (!PARENT_KIND.equals(key.getParent().getKind())) {
            throw new IllegalArgumentException("Key's parent is not a snapshot parent key");
        }
        this.key = key;
    }

    public Entity createEntity(AuthenticatedUser user, Resource resource) {
        Entity entity = new Entity(key);
        entity.setProperty(TIMESTAMP_PROPERTY, System.currentTimeMillis());
        entity.setUnindexedProperty(USER_PROPERTY, user.getId());

        Content.writeProperties(resource, entity);
        return entity;
    }

    public Resource get(DatastoreService datastore) throws EntityNotFoundException {
        Entity entity = datastore.get(key);
        return deserializeResource(entity);
    }

    public Key getParentKey() {
        return key.getParent();
    }

    public static Iterable<Snapshot> getSnapshotsAfter(DatastoreService datastore, long version) {
        Query.FilterPredicate keysFilter = new Query.FilterPredicate(VERSION_PROPERTY, GREATER_THAN, version);
        Query query = new Query(Snapshot.KIND).setFilter(keysFilter).addSort(VERSION_PROPERTY, ASCENDING).setKeysOnly();
        Iterable<Entity> iterable = datastore.prepare(query).asIterable();

        return Iterables.transform(iterable, new Function<Entity, Snapshot>() {
            public Snapshot apply(Entity entity) {
                return new Snapshot(entity.getKey());
            }
        });
    }
}
