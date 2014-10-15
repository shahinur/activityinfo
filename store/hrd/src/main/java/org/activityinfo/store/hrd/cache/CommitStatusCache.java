package org.activityinfo.store.hrd.cache;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.activityinfo.store.hrd.entity.workspace.CommitStatus;
import org.activityinfo.store.hrd.entity.workspace.CommitStatusKey;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.ReadTx;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Instance-level cache of transaction commit status
 */
@Singleton
public class CommitStatusCache {

    private Cache<Long, Long> instanceCache;

    public CommitStatusCache() {
        this.instanceCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();
    }

    public void cache(CommitStatus commitStatus) {
        this.instanceCache.put(commitStatus.getTransactionId(), commitStatus.getCommitVersion());
    }

    public TransactionLevel begin(WorkspaceEntityGroup workspace, ReadTx tx) {
        return new TransactionLevel(workspace, tx);
    }

    public class TransactionLevel {
        public static final long UNCOMMITTED = 0L;

        private WorkspaceEntityGroup workspace;
        private ReadTx tx;
        private Map<Long, Long> map = Maps.newHashMap();

        public TransactionLevel(WorkspaceEntityGroup workspace, ReadTx tx) {
            this.workspace = workspace;
            this.tx = tx;
        }

        public long getVersion(LatestVersion latestVersion) {
            return getVersion(latestVersion.getTransactionId());
        }

        public long getVersion(long transactionId) {
            Long version = map.get(transactionId);
            if(version != null) {
                return version;
            }
            // Try to retrieve from the instance cache
            version = instanceCache.getIfPresent(transactionId);
            if(version != null) {
                map.put(transactionId, version);
                return version;
            }

            // Query datastore
            Optional<CommitStatus> status = tx.getIfExists(new CommitStatusKey(workspace, transactionId));
            if(status.isPresent()) {
                // Once a version is assigned, it never changes, so cache both within the transaction and
                // globally within the instance.
                version = status.get().getCommitVersion();
                map.put(transactionId, version);
                instanceCache.put(transactionId, version);
                return version;
            } else {
                // The transaction will remain uncommitted for the duration of our transaction, but
                // may commit later, ONLY cache on the tx level
                map.put(transactionId, UNCOMMITTED);
                return UNCOMMITTED;
            }
        }

        public boolean isCommitted(long transactionId) {
            return getVersion(transactionId) != UNCOMMITTED;
        }
    }
}
