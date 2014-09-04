package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.*;

/**
 * A single entity which maintains the global version index
 */
public class GlobalVersion {
    public static final long INITIAL_VERSION = 0L;
    public static final String KIND = "GV";
    public static final String KEY_NAME = "current";
    public static final String GLOBAL_VERSION_PROPERTY = "V";

    /**
     *
     * @return the current global version
     */
    public static long getCurrentVersion(DatastoreService datastore, Transaction transaction) {
        try {
            Entity entity = datastore.get(transaction, KeyFactory.createKey(KIND, KEY_NAME));
            return (Long) entity.getProperty(GLOBAL_VERSION_PROPERTY);

        } catch (EntityNotFoundException e) {
            return INITIAL_VERSION;
        }
    }

    /**
     * Increments the global version number
     *
     * @return the new global version number
     */
    public static long incrementVersion(DatastoreService datastore, Transaction tx) {
        Entity entity;
        long version;
        try {
            entity = datastore.get(tx, KeyFactory.createKey(KIND, KEY_NAME));
            version = (Long) entity.getProperty(GLOBAL_VERSION_PROPERTY);

        } catch(EntityNotFoundException e) {
            entity = new Entity(KIND, KEY_NAME);
            version = INITIAL_VERSION;
        }
        version++;
        entity.setProperty(GLOBAL_VERSION_PROPERTY, version);

        datastore.put(tx, entity);

        return version;
    }

}
