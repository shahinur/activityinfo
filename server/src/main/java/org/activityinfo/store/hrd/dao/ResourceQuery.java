package org.activityinfo.store.hrd.dao;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.Resources;
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
    private ReadContext context;

    ResourceQuery(ReadContext context, LatestVersion latestVersion, Authorization authorization, ReadTx tx) {
        this.context = context;
        this.key = latestVersion.getKey();
        this.latestVersion = latestVersion;
        this.authorization = authorization;
        this.tx = tx;

        authorization.assertCanView();
    }

    public UserResource asUserResource() {
        Resource resource = Resources.createResource(latestVersion.getRecord());
        resource.setId(latestVersion.getResourceId());
        resource.setVersion(versionOf(latestVersion));
        resource.setOwnerId(latestVersion.getOwnerId());

        UserResource userResource = new UserResource();
        userResource.setResource(resource);
        userResource.setOwner(authorization.isOwner());
        userResource.setEditAllowed(authorization.canEdit());
        return userResource;
    }

    private long versionOf(LatestVersion latestVersion) {
        if(latestVersion.hasVersion()) {
            return latestVersion.getVersion();
        } else {
            long commitVersion = context.getCommitStatusCache().getVersion(latestVersion);
            if(commitVersion <= 0) {
                throw new IllegalStateException(latestVersion + " is not committed! Should not have arrived here.");
            }
            return commitVersion;
        }
    }

    public ResourceNode asResourceNode() {
        ResourceNode node = new ResourceNode(latestVersion.getResourceId());
        node.setOwnerId(latestVersion.getOwnerId());
        node.setVersion(versionOf(latestVersion));
        node.setLabel(latestVersion.getLabel());
        node.setClassId(latestVersion.getClassId());
        node.setOwner(authorization.isOwner());
        node.setEditAllowed(authorization.canEdit());
        return node;
    }

    public Iterator<Resource> getFormInstances() {
        return tx.query(LatestVersion.formInstancesOf(key)).iterator();
    }

    public Iterable<ResourceNode> queryFolderItems() {
        List<ResourceNode> nodes = Lists.newArrayList();
        for (LatestVersion latestVersion : tx.query(LatestVersion.folderItemsOf(key))) {
            ResourceNode node = new ResourceNode(latestVersion.getResourceId());
            node.setOwnerId(key.getResourceId());
            node.setLabel(latestVersion.getLabel());
            node.setClassId(latestVersion.getClassId());

            if(latestVersion.hasVersion()) {
                node.setVersion(latestVersion.getVersion());
            } else {
                // We have to fetch the full entity
                LatestVersion child = tx.getOrThrow(new LatestVersionKey(key.getWorkspace(), node.getId()));
                node.setVersion(versionOf(child));
            }
            nodes.add(node);
        }
        return nodes;
    }

    public List<Resource> getAccessControlRules() {

        // Only owners can view the ACRs
        authorization.assertIsOwner();

        return Lists.newArrayList(tx.query(AcrEntry.forResource(key)));
    }

    public void assertCanCreateChildren() {
        authorization.assertCanEdit();
    }
}
