package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

import java.util.List;

import static com.google.appengine.api.datastore.TransactionOptions.Builder.withXG;

/**
 * An object which maintains the global version index for use within a single transaction
 */
public class VersionedTransaction implements AutoCloseable {
    private static final Key KEY = KeyFactory.createKey("GV", "current");

    public static final String GLOBAL_VERSION_PROPERTY = "V";

    private final DatastoreService datastoreService;
    private final Transaction transaction;
    private long version;
    private boolean committed;

    public VersionedTransaction(DatastoreService datastoreService) {
        this.datastoreService = datastoreService;

        transaction = datastoreService.beginTransaction(withXG(true));

        try {
            Entity entity = datastoreService.get(transaction, KEY);
            Object property = entity.getProperty(GLOBAL_VERSION_PROPERTY);
            if (property instanceof Long) {
                version = (Long) property;
            }
        } catch(EntityNotFoundException e) {}
    }

    /**
     * Increments the global version number
     * @return the new global version number
     */
    public long incrementVersion() {
        return ++version;
    }

    public Entity get(Key key) throws EntityNotFoundException {
        return datastoreService.get(transaction, key);
    }

    public Key put(Entity entity) {
        return datastoreService.put(transaction, entity);
    }

    public List<Key> put(Iterable<Entity> entities) {
        return datastoreService.put(transaction, entities);
    }

    /**
     * Commit this transaction.
     */
    public void commit() {
        final Entity entity = new Entity(KEY);

        entity.setProperty(GLOBAL_VERSION_PROPERTY, version);
        datastoreService.put(transaction, entity);
        transaction.commit();

        committed = true;
    }

    @Override
    public void close() {
        if (!committed) transaction.rollback();
    }
}
