package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * Datastore entity which holds the current version of the Workspace
 */
public class WorkspaceVersion {

    private static final String KIND = "WV";

    private static final String KEY_NAME = "current";

    private static final String VERSION_PROPERTY = "v";

    private final Key key;

    public WorkspaceVersion(Key rootKey) {
        key = KeyFactory.createKey(rootKey, KIND, KEY_NAME);
    }

    public long get(WorkspaceTransaction tx) {
        try {
            Entity entity = tx.get(key);
            Object property = entity.getProperty(VERSION_PROPERTY);
            if (property instanceof Long) {
                return (Long)property;
            } else {
                return 0;
            }
        } catch(EntityNotFoundException e) {
            return 0;
        }
    }

    public void put(WorkspaceTransaction tx, long newVersion) {
        Entity entity = new Entity(key);
        entity.setProperty(VERSION_PROPERTY, newVersion);
        tx.put(entity);
    }
}
