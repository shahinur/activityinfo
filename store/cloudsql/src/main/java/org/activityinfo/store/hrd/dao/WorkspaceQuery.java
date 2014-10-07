package org.activityinfo.store.hrd.dao;

import com.google.common.base.Optional;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.store.EntityDeletedException;
import org.activityinfo.store.hrd.auth.Authorization;
import org.activityinfo.store.hrd.auth.WorkspaceAuthDAO;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.LatestVersionKey;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.ReadTx;

public class WorkspaceQuery implements AutoCloseable {

    private ReadTx tx;
    private WorkspaceEntityGroup workspace;
    private AuthenticatedUser user;
    private WorkspaceAuthDAO authDao;

    public WorkspaceQuery(WorkspaceEntityGroup workspace, AuthenticatedUser user, ReadTx tx) {
        this.workspace = workspace;
        this.user = user;
        this.tx = tx;
        this.authDao = new WorkspaceAuthDAO(workspace, user, tx);
    }

    public WorkspaceQuery(WorkspaceEntityGroup workspace, AuthenticatedUser user) {
        this(workspace, user, ReadTx.withSerializableConsistency());
    }

    public ResourceQuery getResource(ResourceId id) {

        LatestVersionKey key = new LatestVersionKey(workspace, id);

        // This resource does not exist and has never existed
        // (or has not been committed to the server yet)
        Optional<LatestVersion> latestVersion = tx.getIfExists(key);
        if(!latestVersion.isPresent()) {
            throw new ResourceNotFound(id);
        }

        // Ensure that the user has access to the resource
        Authorization authorization = authDao.forResource(id);
        authorization.assertCanView();

        // Check to see if the resource has been deleted.
        assertNotDeleted(latestVersion.get());

        return new ResourceQuery(latestVersion.get(), authorization, tx);
    }

    private void assertNotDeleted(LatestVersion latestVersion) {
        if (latestVersion.isDeleted() || isAncestorDeleted(latestVersion)) {
            throw new EntityDeletedException();
        }
    }

    private boolean isAncestorDeleted(LatestVersion latestVersion) {
        if(latestVersion.getOwnerId().equals(Resources.ROOT_ID)) {
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
