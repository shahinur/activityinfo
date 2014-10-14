package org.activityinfo.store.hrd.cache;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.store.hrd.entity.workspace.*;
import org.activityinfo.store.hrd.tx.ReadTx;

import javax.inject.Singleton;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cached lookup of the workspace of resource ids.
 */
@Singleton
public class WorkspaceCache {

    private static final Logger LOGGER = Logger.getLogger(WorkspaceCache.class.getName());

    private LoadingCache<ResourceId, ResourceId> cache;

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

    public WorkspaceCache() {
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

    private ResourceId lookupWorkspace(ResourceId resourceId) {
        // First try memcache
        try {
            String workspaceId = (String) memcacheService.get(resourceId.asString());
            if(workspaceId != null) {
                return ResourceId.valueOf(workspaceId);
            }
        } catch(Exception e) {
            LOGGER.log(Level.WARNING, "Exception deserializing workspace from datastore", e);
            // ignore
        }

        // Then query from the datastore
        Optional<LatestVersionKey> latestVersionKey = ReadTx.outsideTransaction().query(LatestVersion.ofResource(resourceId));
        if(latestVersionKey.isPresent()) {
            return latestVersionKey.get().getWorkspace().getWorkspaceId();
        }

        // It is possible for the resource to be committed but not yet appear in the index of all
        // resources. So before giving up, try looking up directly by key.

        if(isWorkspace(resourceId)) {
            return resourceId;
        }

        // Give up
        throw new ResourceNotFound(resourceId);
    }

    private boolean isWorkspace(ResourceId key)  {
        Optional<CurrentVersion> workspaceVersion = ReadTx.outsideTransaction().getIfExists(new CurrentVersionKey(key));
        return workspaceVersion.isPresent();
    }

    private void memcache(ResourceId resourceId, ResourceId workspaceId) {
        try {
            memcacheService.put(resourceId.asString(), workspaceId.asString(), Expiration.byDeltaSeconds(3600));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception putting workspace to memcache", e);
        }
    }

    public WorkspaceEntityGroup lookup(ResourceId resourceId) {
        try {
            return new WorkspaceEntityGroup(cache.get(resourceId));
        } catch (Exception e) {
            return new WorkspaceEntityGroup(lookupWorkspace(resourceId));
        }
    }

    public void cache(ResourceId id, ResourceId workspaceId) {
        memcache(id, workspaceId);
        cache.put(id, workspaceId);
    }

    public void cache(ResourceId id, WorkspaceEntityGroup workspace) {
        cache(id, workspace.getWorkspaceId());
    }
}
