package org.activityinfo.store.hrd.dao;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.store.hrd.StoreContext;
import org.activityinfo.store.hrd.auth.AuthorizationAsserter;
import org.activityinfo.store.hrd.auth.Authorizer;
import org.activityinfo.store.hrd.entity.workspace.*;
import org.activityinfo.store.hrd.index.FolderIndex;
import org.activityinfo.store.hrd.index.FormInstanceIndexer;
import org.activityinfo.store.hrd.tx.ReadWriteTx;
import org.activityinfo.store.hrd.tx.WritableTx;

/**
 *
 */
public class WorkspaceUpdate implements AutoCloseable {


    private StoreContext context;
    private final WorkspaceEntityGroup workspace;
    private final AuthenticatedUser user;
    private final WritableTx tx;
    private final long updateVersion;
    private final AuthorizationAsserter auth;
    private Clock clock;
    private final FormInstanceIndexer formIndexer;

    private boolean committed = false;

    WorkspaceUpdate(StoreContext context, WorkspaceEntityGroup workspace, AuthenticatedUser user,
                    Authorizer auth, WritableTx tx, long updateVersion, Clock clock,
                    FormInstanceIndexer formInstanceIndexer) {
        this.context = context;
        this.workspace = workspace;
        this.user = user;
        this.tx = tx;
        this.updateVersion = updateVersion;
        this.auth = new AuthorizationAsserter(user, auth);
        this.clock = clock;
        this.formIndexer = formInstanceIndexer;
    }

    public WritableTx getTx() {
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

        auth.assertCanCreateChildren(resource.getOwnerId());

        writeSnapshot(resource);
        writeInitialLatestVersion(resource);

        formIndexer.onResourceCreated(resource);

        if(isAcr(resource)) {
            writeAcrIndex(resource);
        }
    }

    /**
     * Writes the updated {@code resource} to the datastore.
     * @param resource the updated version
     */
    public void updateResource(Resource resource) {

        auth.assertCanUpdate(resource.getId());

        updateLatestVersion(resource);
        writeSnapshot(resource);

        if(isAcr(resource)) {
            writeAcrIndex(resource);
        }
    }

    private boolean isAcr(Resource resource) {
        return resource.getClassId().equals(AccessControlRule.CLASS_ID);
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
        latestVersion.setRowIndex(formIndexer.nextInstanceIndex(resource));
        latestVersion.setLabel(FolderIndex.formItemLabel(resource));
        tx.put(latestVersion);
    }

    public void updateAcr(AccessControlRule rule) {
        createOrUpdateResource(rule.asResource());
    }

    private void writeAcrIndex(Resource rule) {
        AcrEntry acr = new AcrEntry(workspace, AccessControlRule.fromResource(rule));
        acr.setVersion(updateVersion);
        tx.put(acr);
    }

    private void updateLatestVersion(Resource resource) {
        LatestVersion latestVersion = tx.getOrThrow(new LatestVersionKey(workspace, resource.getId()));

        if(!latestVersion.hasVersion()) {
            writeInitialSnapshot(latestVersion);
        }

        latestVersion.setOwnerId(resource.getOwnerId());
        latestVersion.setVersion(updateVersion);
        latestVersion.setDeleted(false);
        latestVersion.setRecord(resource.getValue());
        latestVersion.setLabel(FolderIndex.formItemLabel(resource));
        tx.put(latestVersion);
    }

    /**
     * Retroactively write a Snapshot for the <em>initial</em> version of a resource
     * created during a bulk load
     *
     * @param latestVersion
     */
    private void writeInitialSnapshot(LatestVersion latestVersion) {

        Optional<CommitStatus> status = tx.getIfExists(new CommitStatusKey(workspace, latestVersion.getTransactionId()));
        if(!status.isPresent()) {
            // tx is still in progress or has failed, so the resource
            // does not officially exist yet
            throw new ResourceNotFound(latestVersion.getResourceId());
        }

        Snapshot snapshot = new Snapshot(workspace, status.get().getCommitVersion(), latestVersion.getResourceId());
        snapshot.setOwnerId(latestVersion.getOwnerId());
        snapshot.setTimestamp(status.get().getCommitTime());
        snapshot.setUserId(status.get().getUserId());
        snapshot.setValue(latestVersion.getRecord());
        tx.put(snapshot);
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
        formIndexer.flushWrites(updateVersion);
        tx.put(new CurrentVersion(workspace, updateVersion));
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


    public static Builder newBuilder(StoreContext context) {
        return new Builder(context);
    }

    public static Builder newBuilder(StoreContext context, WorkspaceEntityGroup workspace, AuthenticatedUser user) {
        return new Builder(context, workspace, user);
    }

    public WorkspaceEntityGroup getWorkspace() {
        return workspace;
    }


    public static class Builder {

        private StoreContext context;
        private WorkspaceEntityGroup workspace;
        private AuthenticatedUser user;
        private WritableTx tx = null;
        private Authorizer authorizer;
        private long updateVersion = -1L;
        private FormInstanceIndexer formIndexer;
        private Clock clock;

        private Builder(StoreContext context, WorkspaceEntityGroup workspace, AuthenticatedUser user) {
            this.workspace = workspace;
            this.user = user;
            this.context = context;
        }

        private Builder(StoreContext context) {
            Preconditions.checkNotNull(context);

            this.context = context;
        }

        public Builder setFormIndexer(FormInstanceIndexer formIndexer) {
            this.formIndexer = formIndexer;
            return this;
        }

        public Builder setUser(AuthenticatedUser user) {
            this.user = user;
            return this;
        }

        public Builder setWorkspace(WorkspaceEntityGroup workspace) {
            this.workspace = workspace;
            return this;
        }

        public Builder setWorkspace(ResourceId workspaceId) {
            return setWorkspace(new WorkspaceEntityGroup(workspaceId));
        }

        public Builder setTransaction(WritableTx tx) {
            this.tx = tx;
            return this;
        }

        public Builder setAuthorizer(Authorizer authorizer) {
            this.authorizer = authorizer;
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
            Preconditions.checkState(context != null, "context is not set");
            Preconditions.checkState(workspace != null, "workspace is not set");
            Preconditions.checkState(user != null, "user is not set");

            if(tx == null) {
                tx = ReadWriteTx.serializedCrossGroup();
            }
            if(authorizer == null) {
                authorizer = context.createAuthorizer(workspace, user, tx);
            }
            if(updateVersion < 0) {
                updateVersion = fetchLatestVersion() + 1;
            }

            if(clock == null) {
                clock = new Clock() {
                    @Override
                    public long getTime() {
                        return System.currentTimeMillis();
                    }
                };
            }

            if(formIndexer == null) {
                formIndexer = new FormInstanceIndexer(workspace, tx);
            }
            return new WorkspaceUpdate(context, workspace, user,
                authorizer, tx, updateVersion, clock, formIndexer);
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
