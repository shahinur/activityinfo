package org.activityinfo.store.hrd.dao;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.store.hrd.auth.Authorization;
import org.activityinfo.store.hrd.auth.Authorizer;
import org.activityinfo.store.hrd.auth.IsOwner;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.LatestVersionKey;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.ReadWriteTx;

import javax.ws.rs.WebApplicationException;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

public class WorkspaceCreation {

    public static final long INITIAL_VERSION = 1L;

    private final AuthenticatedUser user;

    public WorkspaceCreation(AuthenticatedUser user) {
        this.user = user;
    }


    public void createWorkspace(Resource resource) {

        Preconditions.checkArgument(resource.getOwnerId().equals(Resources.ROOT_ID),
            "workspace owner must be root");

        Preconditions.checkArgument(resource.getValue().getClassId().equals(FolderClass.CLASS_ID),
            "workspaces must have a class id of %s, found %s",
                    FolderClass.CLASS_ID.asString(),
                    resource.getValue().getClassId().asString());

        // Anyone may create a workspace, but you must be logged in.
        if(user.isAnonymous()) {
            throw new WebApplicationException(UNAUTHORIZED);
        }

        try(ReadWriteTx tx = ReadWriteTx.serializedCrossGroup()) {

            assertWorkspaceDoesNotAlreadyExist(tx, resource);

            WorkspaceUpdate workspaceUpdate = new WorkspaceUpdate.Builder(resource.getId(), user)
                .setAuthorizer(new NewlyCreatedWorkspaceAuthorizer())
                .setTransaction(tx)
                .setUpdateVersion(INITIAL_VERSION)
                .begin();

            // Create the actual workspace resource which is just a folder
            workspaceUpdate.createResource(resource);

            // Grant ownership of the workspace to the user.
            AccessControlRule ownerRule = new AccessControlRule(resource.getId(), user.getUserResourceId());
            ownerRule.setOwner(true);
            workspaceUpdate.updateAcr(ownerRule);

            workspaceUpdate.flush();
            tx.commit();
        }
    }

    public void assertWorkspaceDoesNotAlreadyExist(ReadWriteTx tx, Resource resource) {
        Optional<LatestVersion> existing = tx.getIfExists(new LatestVersionKey(
            new WorkspaceEntityGroup(resource.getId()), resource.getId()));

        if(existing.isPresent()) {
            // We'll only accept multiple posts from the owner
            assertInitialVersion(existing.get());

            // Otherwise consider a conflict
            throw new WebApplicationException(CONFLICT);
        }
    }

    private void assertInitialVersion(LatestVersion existing) {
        //TODO
//        // If they're identical, consider this a no-op
//        if(Records.deepEquals(existing.get().getRecord(), resource.getValue())) {
//            return;
//        }
    }

    private static class NewlyCreatedWorkspaceAuthorizer implements Authorizer {

        @Override
        public Authorization forResource(ResourceId id) {
            return new IsOwner();
        }
    }
}
