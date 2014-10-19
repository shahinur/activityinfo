package org.activityinfo.store.hrd.dao;

import com.google.appengine.api.datastore.FetchOptions;
import com.google.common.collect.Lists;
import org.activityinfo.model.resource.*;
import org.activityinfo.store.hrd.auth.ResourceAsserter;
import org.activityinfo.store.hrd.entity.workspace.AcrEntry;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.LatestVersionKey;
import org.activityinfo.store.hrd.entity.workspace.Snapshot;
import org.activityinfo.store.hrd.tx.ReadTx;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ResourceQuery {
    private final LatestVersionKey key;
    private final ResourceAsserter authorization;
    private final ReadTx tx;
    private LatestVersion latestVersion;
    private ReadContext context;

    ResourceQuery(ReadContext context, LatestVersion latestVersion, ResourceAsserter authorization, ReadTx tx) {
        this.context = context;
        this.key = latestVersion.getKey();
        this.latestVersion = latestVersion;
        this.authorization = authorization;
        this.tx = tx;

        authorization.assertCanView();
    }

    public UserResource asUserResource() {
        Resource resource = Resources.createResource();
        resource.setId(latestVersion.getResourceId());
        resource.setVersion(versionOf(latestVersion));
        resource.setOwnerId(latestVersion.getOwnerId());
        resource.setValue(latestVersion.getRecord());

        UserResource userResource = new UserResource();
        userResource.setResource(resource);
        userResource.setOwner(authorization.get().isOwner());
        userResource.setEditAllowed(authorization.get().canUpdate());
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
        node.setOwner(authorization.get().isOwner());
        node.setEditAllowed(authorization.get().canUpdate());
        return node;
    }

    public Iterator<Resource> getFormInstances() {
        FetchOptions options = FetchOptions.Builder.withChunkSize(250);
        return tx.query(LatestVersion.formInstancesOf(key), options).iterator();
    }

    public Iterable<ResourceNode> getFolderItems() {
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
        authorization.assertCanViewAcrs();

        return Lists.newArrayList(tx.query(AcrEntry.forResource(key)));
    }

    public void assertCanCreateChildren() {
        authorization.assertCanUpdate();
    }

    public List<ResourceVersion> getSnapshots() {
        List<ResourceVersion> resources = Lists.newArrayList();
        for(Snapshot snapshot : tx.query(Snapshot.of(context.getWorkspace(), key.getResourceId()))) {
            ResourceVersion version = new ResourceVersion();
            version.setResourceId(snapshot.getResourceId());
            version.setDateCommitted(new Date(snapshot.getTimestamp()));
            version.setVersion(snapshot.getVersion());
            version.setUserId((int) snapshot.getUserId());
            resources.add(version);
        }
        return resources;
    }
}
