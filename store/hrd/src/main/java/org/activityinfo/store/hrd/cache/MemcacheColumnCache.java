package org.activityinfo.store.hrd.cache;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.service.tables.ColumnCache;
import org.activityinfo.store.hrd.HrdStoreReader;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemcacheColumnCache implements ColumnCache {

    public static final Logger LOGGER = Logger.getLogger(MemcacheColumnCache.class.getName());

    private HrdStoreReader.Accessor storeAccessor;
    private MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

    public MemcacheColumnCache(final HrdStoreReader.Accessor storeAccessor) {
        this.storeAccessor = storeAccessor;
    }


    private long formCacheKey(ResourceId formClassId) {
        return storeAccessor.getFormMetadata(formClassId).getCacheKey();
    }

    @Override
    public Map<String, ColumnView> getIfPresent(ResourceId formClassId, Set<String> columnKeys) {
        try {
            List<ColumnCacheKey> cacheKeys = Lists.newArrayList();

            long formCacheKey = formCacheKey(formClassId);
            for(String columnKey : columnKeys) {
                cacheKeys.add(new ColumnCacheKey(formClassId, formCacheKey, columnKey));
            }

            Map<String, ColumnView> result = new HashMap<>();
            for (Map.Entry<ColumnCacheKey, Object> entry : memcacheService.getAll(cacheKeys).entrySet()) {
                result.put(entry.getKey().getColumnKey(), (ColumnView)entry.getValue());
            }
            assertConsistentRowCount(formClassId, result);

            return result;

        } catch(Exception e) {
            LOGGER.log(Level.WARNING, "Error fetching column views from memcache: " + e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    @Override
    public void put(ResourceId formClassId, Map<String, ? extends Supplier<ColumnView>> columnMap) {
        Map<ColumnCacheKey, ColumnView> toCache = new HashMap<>();
        long formCacheKey = formCacheKey(formClassId);
        for (Map.Entry<String, ? extends Supplier<ColumnView>> entry : columnMap.entrySet()) {
            toCache.put(new ColumnCacheKey(formClassId, formCacheKey, entry.getKey()), entry.getValue().get());
        }
        memcacheService.putAll(toCache, Expiration.byDeltaSeconds((int) TimeUnit.HOURS.toSeconds(6)));
    }

    private void assertConsistentRowCount(ResourceId formClassId, Map<String, ColumnView> views) {
        if(!views.isEmpty()) {
            Iterator<ColumnView> it = views.values().iterator();
            int numRows = it.next().numRows();

            while (it.hasNext()) {
                if (numRows != it.next().numRows()) {
                    LOGGER.log(Level.SEVERE, "Inconsistent row counts found for " + formClassId);
                    for (Map.Entry<String, ColumnView> entry : views.entrySet()) {
                        LOGGER.log(Level.SEVERE, entry.getKey() + " = " + entry.getValue().numRows());
                    }
                    throw new IllegalStateException("Inconsistent row counts in query");
                }
            }
        }
    }
}
