package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Entity;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.hrd.tx.IsEntity;

/**
 * Datastore entity that stores metadata on FormClass created by users
 */
public class FormMetaEntry implements IsEntity {

    private static final String COUNT_PROPERTY = "count";
    private static final String CACHE_KEY_PROPERTY = "version";

    private final FormMetaEntryKey key;
    private long instanceCount;
    private long cacheKey;

    public FormMetaEntry(FormMetaEntryKey key, Entity entity) {
        this.key = key;
        this.instanceCount = (Long)entity.getProperty(COUNT_PROPERTY);
        this.cacheKey = (Long)entity.getProperty(CACHE_KEY_PROPERTY);
    }

    public FormMetaEntry(WorkspaceEntityGroup workspace, ResourceId formClassId) {
        this.key = new FormMetaEntryKey(workspace, formClassId);
    }

    public FormMetaEntryKey getKey() {
        return key;
    }

    /**
     * @return the number of instances of this FormClass that have been created, including those subsequently
     * deleted.
     */
    public long getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(long instanceCount) {
        this.instanceCount = instanceCount;
    }

    /**
     *
     * @return the cache key for this FormClass.
     */
    public long getCacheKey() {
        return cacheKey;
    }

    /**
     * Updates the cache key of this FormClass. The Cache key should be equal to the maximum
     * version number of the FormClass or any of its instances.
     */
    public void setCacheKey(long cacheKey) {
        this.cacheKey = cacheKey;
    }

    @Override
    public Entity toEntity() {
        Entity entity = new Entity(key.unwrap());
        entity.setProperty(CACHE_KEY_PROPERTY, cacheKey);
        entity.setProperty(COUNT_PROPERTY, instanceCount);
        return entity;
    }

    /**
     * Increments the instance count
     *
     * @return the new count
     */
    public long incrementInstanceCount() {
        return ++instanceCount;
    }
}
