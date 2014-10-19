package org.activityinfo.store.hrd.dao;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.store.hrd.StoreContext;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.LatestVersionKey;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.ReadTx;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;

public class WorkspaceQuery implements AutoCloseable {

    private final ReadContext context;
    private ReadTx tx;
    private LoadingCache<ResourceId, Optional<ResourceQuery>> resourceCache;

    public WorkspaceQuery(final StoreContext context, WorkspaceEntityGroup workspace, AuthenticatedUser user, ReadTx tx) {
        this.context = new ReadContext(context, workspace, user, tx);
        this.tx = tx;
        this.resourceCache = CacheBuilder.newBuilder().maximumSize(50).build(new ResourceLoader());
    }

    public WorkspaceQuery(StoreContext context, WorkspaceEntityGroup workspace, AuthenticatedUser user) {
        this(context, workspace, user, ReadTx.withSerializableConsistency());
    }

    private class ResourceLoader extends CacheLoader<ResourceId, Optional<ResourceQuery>> {

        @Override
        public Optional<ResourceQuery> load(@Nonnull ResourceId id) throws Exception {

            LatestVersionKey key = new LatestVersionKey(context.getWorkspace(), id);

            // This resource does not exist and has never existed
            // (or has not been committed to the server yet)
            Optional<LatestVersion> latestVersion = tx.getIfExists(key);
            if (latestVersion.isPresent() && isCommitted(latestVersion.get())) {
                return Optional.of(new ResourceQuery(context, latestVersion.get()));

            } else {
                return Optional.absent();
            }
        }

        private boolean isCommitted(LatestVersion latestVersion) {
            if (latestVersion.hasVersion()) {
                return true;
            } else {
                // this resource was part of a bulk commit, we have to see whether
                // the transaction has been completed yet.
                return context.getCommitStatusCache().isCommitted(latestVersion.getTransactionId());
            }
        }
    }

    public ResourceQuery getResource(ResourceId id) {

        Optional<ResourceQuery> resourceQuery;
        try {
            resourceQuery = resourceCache.get(id);
        } catch (ExecutionException e) {
            throw new RuntimeException("Exception loading resource " + id, e);
        }

        if(!resourceQuery.isPresent()) {
            throw new ResourceNotFound(id);
        }

        resourceQuery.get().assertCanView();
        resourceQuery.get().assertNotDeleted();

        return resourceQuery.get();
    }

    @Override
    public void close() {
        tx.close();
    }

}
