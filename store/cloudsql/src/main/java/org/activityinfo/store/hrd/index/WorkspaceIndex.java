package org.activityinfo.store.hrd.index;

import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.collect.Lists;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.store.hrd.dao.Interceptor;
import org.activityinfo.store.hrd.dao.UpdateInterceptor;
import org.activityinfo.store.hrd.entity.user.UserWorkspace;
import org.activityinfo.store.hrd.entity.user.UserWorkspaceKey;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.LatestVersionKey;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.ReadTx;
import org.activityinfo.store.hrd.tx.ReadWriteTx;

import java.util.List;

/**
 * Index of workspaces owned, shared with, and starred by users.
 */
public class WorkspaceIndex extends Interceptor {

    public static final String PARENT_KIND = "User";

    public static final String INDEX_KIND = "Workspace";

    public static final String OWNED_PROPERTY = "own";

    public static final String STARRED_PROPERTY = "star";


    public static Key parentKey(AuthenticatedUser user) {
        return KeyFactory.createKey(PARENT_KIND, user.getUserResourceId().asString());
    }

    @Override
    public UpdateInterceptor createUpdateInterceptor(final WorkspaceEntityGroup workspace,
                                                     final AuthenticatedUser user,
                                                     final ReadWriteTx transaction) {
        return new UpdateInterceptor() {
            @Override
            public void onResourceCreated(LatestVersion latestVersion) {
                if(latestVersion.getOwnerId().equals(Resources.ROOT_ID)) {
                    // Add an entry to the user's list of workspaces
                    // So that we can find it back.
                    UserWorkspace entry = new UserWorkspace(user, workspace);
                    entry.setOwned(true);
                    transaction.put(entry);
                }
            }
        };
    }

    public static List<ResourceNode> queryUserWorkspaces(AuthenticatedUser user) {

        List<LatestVersionKey> workspaceKeys = Lists.newArrayList();
        try(ReadTx tx = ReadTx.withSerializableConsistency()) {
            for (UserWorkspaceKey key : tx.query(UserWorkspace.ofUser(user), FetchOptions.Builder.withLimit(100))) {
                workspaceKeys.add(key.workspaceResourceKey());
            }
        }

        List<ResourceNode> nodes = Lists.newArrayList();
        try(ReadTx tx = ReadTx.outsideTransaction()) {
            for(LatestVersion workspace : tx.getList(workspaceKeys)) {
                ResourceNode node = new ResourceNode(workspace.toResource());
                node.setOwner(true);
                node.setEditAllowed(true);
                nodes.add(node);
            }
        }
        return nodes;
    }
}
