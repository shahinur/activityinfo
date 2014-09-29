package org.activityinfo.store.hrd.index;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.store.hrd.entity.LatestContent;
import org.activityinfo.store.hrd.entity.Workspace;

import java.util.Iterator;
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
                    ResourceId workspaceId = lookupWorkspace(key);
                    memcache(key, workspaceId);
                    return workspaceId;
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

        Iterator<Entity> indexEntry = datastore.prepare(query).asIterator();

        // Check to see if the resource is a workspace in parallel
        if(isWorkspace(key)) {
            return key;
        }

        if(indexEntry.hasNext()) {
            return LatestContent.workspaceFromKey(indexEntry.next().getKey());
        }

        // Give up
        throw new ResourceNotFound(key);
    }

    private boolean isWorkspace(ResourceId key)  {
        try {
            datastore.get(null, new Workspace(key).getLatestContent(key).getKey());
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
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
        } catch (Exception e) {
            return new Workspace(lookupWorkspace(resourceId));
        }
    }

    public void cache(ResourceId id, Workspace workspace) {
        memcache(id, workspace.getWorkspaceId());
        cache.put(id, workspace.getWorkspaceId());
    }
}
