package org.activityinfo.store.hrd.dao;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.store.hrd.auth.Authorizer;
import org.activityinfo.store.hrd.auth.WorkspaceAuthDAO;
import org.activityinfo.store.hrd.entity.workspace.*;
import org.activityinfo.store.hrd.tx.ReadWriteTx;

/**
 *
 */
public class WorkspaceUpdate implements AutoCloseable {


    private final WorkspaceEntityGroup workspace;
    private final UpdateInterceptor interceptors;
    private final AuthenticatedUser user;
    private final ReadWriteTx tx;
    private final long updateVersion;
    private final Authorizer auth;
    private Clock clock;

    private boolean committed = false;

    private boolean dirty;

    WorkspaceUpdate(WorkspaceEntityGroup workspace, UpdateInterceptor interceptors, AuthenticatedUser user,
                    Authorizer auth, ReadWriteTx tx, long updateVersion, Clock clock) {
        this.workspace = workspace;
        this.interceptors = interceptors;
        this.user = user;
        this.tx = tx;
        this.updateVersion = updateVersion;
        this.auth = auth;
        this.clock = clock;
    }

    public ReadWriteTx getTx() {
        return tx;
    }

    private VersionKey updateVersionKey() {
        return new VersionKey(workspace, updateVersion);
    }

    public long getUpdateVersion() {
        return updateVersion;
    }

    public void createOrUpdateResource(Resource resource) {

        Optional<LatestVersion> latest = tx.getIfExists(new LatestVersionKey(workspace, resource.getId()));

        if(latest.isPresent()) {
            updateResource(resource);

        } else {
            createResource(resource);
        }
    }


    /**
     * Writes the updated {@code resource} to the datastore.
     * @param resource the updated version
     */
    public void createResource(Resource resource) {
        Preconditions.checkNotNull(resource.getOwnerId());

        writeSnapshot(resource);
        writeInitialLatestVersion(resource);

        dirty = true;
    }


    /**
     * Writes the updated {@code resource} to the datastore.
     * @param resource the updated version
     */
    public void updateResource(Resource resource) {

        auth.forResource(resource.getId()).assertCanEdit();

        updateLatestVersion(resource);
        writeSnapshot(resource);

        dirty = true;
    }

    private void writeSnapshot(Resource resource) {
        Snapshot snapshot = new Snapshot(updateVersionKey(), resource.getId());
        snapshot.setOwnerId(resource.getOwnerId());
        snapshot.setTimestamp(clock.getTime());
        snapshot.setUserId(user.getId());
        snapshot.setValue(resource.getValue());
        tx.put(snapshot);
    }

    private void writeInitialLatestVersion(Resource resource) {
        LatestVersion latestVersion = new LatestVersion(workspace, resource);
        latestVersion.setVersion(updateVersion);
        interceptors.onResourceCreated(latestVersion);
        tx.put(latestVersion);
    }


    public void updateAcr(AccessControlRule rule) {
        AcrEntry acr = new AcrEntry(workspace, rule);
        tx.put(acr);
    }

    private void updateLatestVersion(Resource resource) {
        LatestVersion latestVersion = tx.getOrThrow(new LatestVersionKey(workspace, resource.getId()));
        latestVersion.set(resource);
        latestVersion.setDeleted(false);
        interceptors.onResourceUpdated(latestVersion);
        tx.put(latestVersion);
    }

    public void delete(ResourceId resourceId) {

        Optional<LatestVersion> previousVersion = tx.getIfExists(new LatestVersionKey(workspace, resourceId));
        if(!previousVersion.isPresent()) {
            throw new ResourceNotFound(resourceId);
        }

        // Create a new snapshot entry of the resource to log its deletion
        writeSnapshotOfDeletedResource(previousVersion.get());
        writeDeletedLatestVersion(previousVersion.get());
    }

    private void writeDeletedLatestVersion(LatestVersion previousVersion) {
        LatestVersion latestVersion = new LatestVersion(previousVersion);
        latestVersion.setDeleted(true);
        latestVersion.setVersion(updateVersion);
        tx.put(latestVersion);
    }

    private void writeSnapshotOfDeletedResource(ResourceVersion previousVersion) {
        Snapshot snapshot = new Snapshot(updateVersionKey(), previousVersion.getResourceId());
        snapshot.setOwnerId(previousVersion.getOwnerId());
        snapshot.setTimestamp(System.currentTimeMillis());
        snapshot.setUser(user);
        snapshot.setDeleted(true);
        tx.put(snapshot);
    }

    public void flush() {
        if(dirty) {
            interceptors.flush(updateVersion);
            tx.put(new CurrentVersion(workspace, updateVersion));
        }
    }

    public void commit() {
        flush();
        tx.commit();
        committed = true;
    }


    @Override
    public void close() {
        if(!committed) {
            tx.rollback();
        }
    }

    public static Builder build(WorkspaceEntityGroup workspace, AuthenticatedUser user) {
        return new Builder(workspace, user);
    }

    public static class Builder {

        private final WorkspaceEntityGroup workspace;
        private AuthenticatedUser user;
        private ReadWriteTx tx = null;
        private Authorizer authorizer;
        private UpdateInterceptor updateInterceptor;
        private long updateVersion = -1L;
        private Clock clock;

        public Builder(WorkspaceEntityGroup workspace, AuthenticatedUser user) {
            this.workspace = workspace;
            this.user = user;
        }

        public Builder(ResourceId workspaceId, AuthenticatedUser user) {
            this.workspace = new WorkspaceEntityGroup(workspaceId);
            this.user = user;
        }

        public Builder setTransaction(ReadWriteTx tx) {
            this.tx = tx;
            return this;
        }

        public Builder setAuthorizer(Authorizer authorizer) {
            this.authorizer = authorizer;
            return this;
        }

        public Builder setUpdateInterceptor(UpdateInterceptor updateInterceptor) {
            this.updateInterceptor = updateInterceptor;
            return this;
        }

        public Builder setUpdateVersion(long version) {
            this.updateVersion = version;
            return this;
        }

        public Builder setClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public WorkspaceUpdate begin() {
            if(tx == null) {
                tx = ReadWriteTx.serializedCrossGroup();
            }
            if(authorizer == null) {
                authorizer = new WorkspaceAuthDAO(workspace, user, tx);
            }
            if(updateVersion < 0) {
                updateVersion = fetchLatestVersion();
            }
            if(updateInterceptor == null) {
                updateInterceptor = new InterceptorSet().createUpdateInterceptor(workspace, user, tx);
            }

            if(clock == null) {
                clock = new Clock() {
                    @Override
                    public long getTime() {
                        return System.currentTimeMillis();
                    }
                };
            }

            return new WorkspaceUpdate(workspace, updateInterceptor, user, authorizer, tx, updateVersion, clock);
        }

        private long fetchLatestVersion() {
            Optional<CurrentVersion> currentVersion = tx.getIfExists(new CurrentVersionKey(workspace));
            if(!currentVersion.isPresent()) {
                throw new IllegalStateException("Workspace does not exist yet.");
            }
            return currentVersion.get().getCurrentVersion();
        }
    }
}
