package org.activityinfo.store.hrd.dao;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.store.hrd.StoreContext;
import org.activityinfo.store.hrd.auth.NullAuthorizer;
import org.activityinfo.store.hrd.entity.user.UserWorkspace;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.LatestVersionKey;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.ReadWriteTx;

import javax.ws.rs.WebApplicationException;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

public class WorkspaceCreation {

    public static final long INITIAL_VERSION = 1L;

    private final StoreContext context;
    private final AuthenticatedUser user;


    public WorkspaceCreation(StoreContext context, AuthenticatedUser user) {
        this.context = context;
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

        WorkspaceEntityGroup workspace = new WorkspaceEntityGroup(resource.getId());

        try(ReadWriteTx tx = ReadWriteTx.serializedCrossGroup()) {

            assertWorkspaceDoesNotAlreadyExist(tx, resource);

            WorkspaceUpdate workspaceUpdate = WorkspaceUpdate.newBuilder(context)
                .setWorkspace(workspace)
                .setUser(user)
                .setAuthorizer(new NullAuthorizer())
                .setTransaction(tx)
                .setUpdateVersion(INITIAL_VERSION)
                .begin();

            // Create the actual workspace resource which is just a folder
            workspaceUpdate.createResource(resource);

            // Grant ownership of the workspace to the user.
            AccessControlRule ownerRule = new AccessControlRule(resource.getId(), user.getUserResourceId());
            ownerRule.setOwner(true);
            workspaceUpdate.updateAcr(ownerRule);


            // Add an entry to the user's list of workspaces
            // So that we can find it back.
            UserWorkspace entry = new UserWorkspace(user, workspace);
            entry.setOwned(true);
            tx.put(entry);

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


}
