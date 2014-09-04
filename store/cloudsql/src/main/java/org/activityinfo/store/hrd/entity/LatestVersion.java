package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import static org.activityinfo.store.hrd.entity.Content.VERSION_PROPERTY;

/**
 * An entity within the {@code ResourceGroup}  which stores
 * only the current version of the resource
 */
public class LatestVersion {

    public static final String KIND = "CV";

    public static final long KEY_ID = 1;

    private final ResourceGroup group;
    private final Key key;

    public LatestVersion(ResourceGroup group) {
        this.group = group;
        this.key = KeyFactory.createKey(group.getKey(), KIND, KEY_ID);
    }

    public long get(VersionedTransaction versionedTransaction) {
        try {
            Entity entity = versionedTransaction.get(key);
            return (Long) entity.getProperty(VERSION_PROPERTY);
        } catch (EntityNotFoundException e) {
            return 0;
        }
    }

    public Entity createEntity(long version) {
        Entity entity = new Entity(key);
        entity.setUnindexedProperty(VERSION_PROPERTY, version);
        return entity;
    }
}
