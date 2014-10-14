package org.activityinfo.store.hrd.index;

import com.google.appengine.api.datastore.FetchOptions;
import com.google.common.collect.Lists;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.store.hrd.entity.user.UserWorkspace;
import org.activityinfo.store.hrd.entity.user.UserWorkspaceKey;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.LatestVersionKey;
import org.activityinfo.store.hrd.tx.ReadTx;

import java.util.List;

/**
 * Index of workspaces owned, shared with, and starred by users.
 */
public class WorkspaceIndex {


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
