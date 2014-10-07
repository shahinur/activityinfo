package org.activityinfo.store.hrd.dao;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.store.hrd.auth.Authorization;
import org.activityinfo.store.hrd.entity.workspace.AcrEntry;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.LatestVersionKey;
import org.activityinfo.store.hrd.tx.ReadTx;

import java.util.Iterator;
import java.util.List;

public class ResourceQuery {
    private final LatestVersionKey key;
    private final Authorization authorization;
    private final ReadTx tx;
    private LatestVersion latestVersion;

    ResourceQuery(LatestVersion latestVersion, Authorization authorization, ReadTx tx) {
        this.key = latestVersion.getKey();
        this.latestVersion = latestVersion;
        this.authorization = authorization;
        this.tx = tx;

        authorization.assertCanView();
    }

    public UserResource asUserResource() {
        UserResource resource = new UserResource();
        resource.setOwner(authorization.isOwner());
        resource.setEditAllowed(authorization.canEdit());
        resource.setResource(latestVersion.toResource());
        return resource;
    }

    public ResourceNode asResourceNode() {
        ResourceNode node = new ResourceNode(latestVersion.toResource());
        node.setOwner(authorization.isOwner());
        node.setEditAllowed(authorization.canEdit());
        return node;
    }

    public Iterator<Resource> getFormInstances() {
        return tx.query(LatestVersion.formInstancesOf(key)).iterator();
    }

    public Iterable<ResourceNode> queryFolderItems() {
        return tx.query(LatestVersion.folderItemsOf(key));
    }

    public List<Resource> getAccessControlRules() {

        // Only owners can view the ACRs
        authorization.assertIsOwner();

        return Lists.newArrayList(tx.query(AcrEntry.forResource(key)));
    }
}
