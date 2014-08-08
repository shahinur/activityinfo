package org.activityinfo.store.cloudsql;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceNode;

import javax.annotation.Nullable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Domain-specific wrapper around {@link com.google.appengine.api.memcache.MemcacheService}
 */
public class StoreCache {

    private static final Logger LOGGER = Logger.getLogger(StoreCache.class.getName());

    private final MemcacheService memcacheService;

    private final JsonParser jsonParser = new JsonParser();

    public StoreCache(MemcacheService memcacheService) {
        this.memcacheService = memcacheService;
    }


    /**
     * Un/Marshalls objects to/from a form suitable for MemCache which normally
     * requires serializable data types.
     *
     * @param <T> the domain type
     */
    private interface Marshaller<T> {

        Object marshall(T value);
        T unmarshall(Object value);
    }

    private Marshaller NULL_MARSHALLER = new Marshaller() {
        @Override
        public Object marshall(Object value) {
            return value;
        }

        @Override
        public Object unmarshall(Object value) {
            return value;
        }
    };


    private Marshaller<ResourceNode> RESOURCE_NODE = new Marshaller<ResourceNode>() {

        @Override
        public Object marshall(ResourceNode value) {
            return value.toJson().toString();
        }

        @Override
        public ResourceNode unmarshall(Object value) {
            JsonObject jsonObject = jsonParser.parse((String)value).getAsJsonObject();
            return ResourceNode.fromJson(jsonObject);
        }
    };


    public class CacheItem<T> {
        private final String key;
        private Marshaller<T> marshaller;

        public CacheItem(String key) {
            this.key = key;
            this.marshaller = NULL_MARSHALLER;
        }

        public CacheItem(String key, Marshaller<T> marshaller) {
            this.key = key;
            this.marshaller = marshaller;
        }

        @Nullable
        public T get() {
            try {
                Object value = memcacheService.get(key);
                if (value != null) {
                    return marshaller.unmarshall(value);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Exception while fetching key [" + key + "] from memcache", e);
            }
            return null;
        }

        public void put(T value) {
            try {
                memcacheService.put(key, marshaller.marshall(value));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Exception while caching key [" + key + "] to memcache", e);
            }
        }
    }

    public CacheItem<Long> subTreeVersion(ResourceId resourceId) {
        return new CacheItem<>("rc:subtree:" + resourceId.asString());
    }

    public CacheItem<ResourceNode> resourceNode(ResourceId resourceId, long subTreeVersion) {
        return new CacheItem<>("rc:node:" + resourceId.asString() + ":" + subTreeVersion, RESOURCE_NODE);
    }

    public void put(ResourceNode node) {
        resourceNode(node.getId(), node.getSubTreeVersion()).put(node);
    }
}