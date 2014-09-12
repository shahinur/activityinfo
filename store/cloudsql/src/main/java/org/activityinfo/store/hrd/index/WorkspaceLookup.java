package org.activityinfo.store.hrd.index;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.hrd.entity.LatestContent;
import org.activityinfo.store.hrd.entity.Workspace;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cached lookup of the workspace of resource ids.
 */
public class WorkspaceLookup {

    private static final Logger LOGGER = Logger.getLogger(WorkspaceLookup.class.getName());

    private LoadingCache<ResourceId, ResourceId> cache;

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

    public WorkspaceLookup() {
        cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(new CacheLoader<ResourceId, ResourceId>() {
                @Override
                public ResourceId load(ResourceId key) throws Exception {
                    return lookupWorkspace(key);
                }
            });
    }

    private ResourceId lookupWorkspace(ResourceId key) {
        // First try memcache
        try {
            String workspaceId = (String) memcacheService.get(key.asString());
            if(workspaceId != null) {
                return ResourceId.valueOf(workspaceId);
            }
        } catch(Exception e) {
            LOGGER.log(Level.WARNING, "Exception deserializing workspace from datastore", e);
            // ignore
        }

        // Then query from the datastore
        Query query = new Query(LatestContent.KIND)
            .setKeysOnly()
            .setFilter(new Query.FilterPredicate(LatestContent.RESOURCE_ID_PROPERTY,
                                    Query.FilterOperator.EQUAL, key.asString()));

        Entity entity = datastore.prepare(query).asSingleEntity();

        if(entity == null) {
            throw new IllegalStateException(
                "Failed to retrieve resource [" + key.asString() + "] entity using 'ID' index");
        }

        ResourceId workspaceId = LatestContent.workspaceFromKey(entity.getKey());


        // ...and cache in memcache
        memcache(key, workspaceId);

        return workspaceId;
    }

    private void memcache(ResourceId resourceId, ResourceId workspaceId) {
        try {
            memcacheService.put(resourceId.asString(), workspaceId.asString(), Expiration.byDeltaSeconds(3600));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception putting workspace to memcache", e);
        }
    }

    public Workspace lookup(ResourceId resourceId) {
        try {
            return new Workspace(cache.get(resourceId));
        } catch (ExecutionException e) {
            return new Workspace(lookupWorkspace(resourceId));
        }
    }

    public void cache(ResourceId id, Workspace workspace) {
        memcache(id, workspace.getWorkspaceId());
        cache.put(id, workspace.getWorkspaceId());
    }
}
