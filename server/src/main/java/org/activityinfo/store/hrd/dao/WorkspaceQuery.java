package org.activityinfo.store.hrd.dao;

import com.google.common.base.Optional;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.store.ResourceDeletedException;
import org.activityinfo.store.hrd.StoreContext;
import org.activityinfo.store.hrd.auth.Authorization;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.LatestVersionKey;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.ReadTx;

public class WorkspaceQuery implements AutoCloseable {

    private final ReadContext context;
    private ReadTx tx;

    public WorkspaceQuery(StoreContext context, WorkspaceEntityGroup workspace, AuthenticatedUser user, ReadTx tx) {
        this.context = new ReadContext(context, workspace, user, tx);
        this.tx = tx;
    }

    public WorkspaceQuery(StoreContext context, WorkspaceEntityGroup workspace, AuthenticatedUser user) {
        this(context, workspace, user, ReadTx.withSerializableConsistency());
    }

    public ResourceQuery getResource(ResourceId id) {

        LatestVersionKey key = new LatestVersionKey(context.getWorkspace(), id);

        // This resource does not exist and has never existed
        // (or has not been committed to the server yet)
        Optional<LatestVersion> latestVersion = tx.getIfExists(key);
        if(!latestVersion.isPresent()) {
            throw new ResourceNotFound(id);
        }

        assertCommitted(latestVersion.get());

        // Ensure that the user has access to the resource
        Authorization authorization = context.authorizationFor(id);
        authorization.assertCanView();

        // Check to see if the resource has been deleted.
        assertNotDeleted(latestVersion.get());

        return new ResourceQuery(context, latestVersion.get(), authorization, tx);
    }

    private void assertCommitted(LatestVersion latestVersion) {
        if (!latestVersion.hasVersion()) {
            // this resource was part of a bulk commit, we have to see whether
            // the transaction has been completed yet.
            if(!context.getCommitStatusCache().isCommitted(latestVersion.getTransactionId())) {
                throw new ResourceNotFound();
            }
        }
    }

    private void assertNotDeleted(LatestVersion latestVersion) {
        if (latestVersion.isDeleted() || isAncestorDeleted(latestVersion)) {
            throw new ResourceDeletedException();
        }
    }

    private boolean isAncestorDeleted(LatestVersion latestVersion) {
        if(latestVersion.getOwnerId().equals(Resources.ROOT_RESOURCE_ID)) {
            return false;
        }
        LatestVersion owner = tx.getOrThrow(latestVersion.getOwnerLatestVersionKey());
        if(owner.isDeleted()) {
            return true;
        } else {
            return isAncestorDeleted(owner);
        }
    }

    @Override
    public void close() {
        tx.close();
    }

}
