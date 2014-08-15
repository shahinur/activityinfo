package org.activityinfo.store.cloudsql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.memcache.MemcacheService;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Domain-specific wrapper around {@link com.google.appengine.api.memcache.MemcacheService}
 */
public class StoreCache {

    private static final Logger LOGGER = Logger.getLogger(StoreCache.class.getName());

    private final MemcacheService memcacheService;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

        Object marshall(T value) throws JsonProcessingException;
        T unmarshall(Object value) throws IOException;
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
        public Object marshall(ResourceNode value) throws JsonProcessingException {
            return objectMapper.writeValueAsString(value);
        }

        @Override
        public ResourceNode unmarshall(Object value) throws IOException {
            return objectMapper.readValue((String) value, ResourceNode.class);
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