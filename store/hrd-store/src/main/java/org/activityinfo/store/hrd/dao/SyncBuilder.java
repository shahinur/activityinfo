package org.activityinfo.store.hrd.dao;

import com.google.apphosting.api.ApiProxy;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.hrd.auth.WorkspaceAuthDAO;
import org.activityinfo.store.hrd.entity.workspace.Snapshot;
import org.activityinfo.store.hrd.entity.workspace.SnapshotKey;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.ReadTx;

import java.util.List;
import java.util.Map;


public class SyncBuilder {

    private final static long TIME_LIMIT_MILLISECONDS = 10 * 1000L;

    private final WorkspaceAuthDAO auth;
    private ReadTx tx;
    private WorkspaceEntityGroup workspace;
    private long clientVersion;


    public SyncBuilder(ResourceId workspace, AuthenticatedUser user) {
        this.workspace = new WorkspaceEntityGroup(workspace);
        this.tx = ReadTx.withSerializableConsistency();
        this.auth = new WorkspaceAuthDAO(this.workspace, user, tx);
    }

    public List<Resource> getUpdates(long clientVersion) {
        this.clientVersion = clientVersion;

        Map<ResourceId, SnapshotKey> snapshots = Maps.newLinkedHashMap();

        for (SnapshotKey snapshot : tx.query(Snapshot.afterVersion(workspace, this.clientVersion))) {
            ResourceId resourceId = snapshot.getResourceId();

            // We want the linked list to be sorted based on the most recent insertion of a resource
            snapshots.remove(resourceId);
            snapshots.put(resourceId, snapshot);


            if (outOfTime()) {
                break;
            }
        }

        List<Resource> resources = Lists.newArrayListWithCapacity(snapshots.size());

        for (SnapshotKey snapshot : snapshots.values()) {
            Resource resource = tx.getOrThrow(snapshot).toResource();

            if(auth.forResource(resource.getId()).canView()) {
                resources.add(resource);
            }

            if (AccessControlRule.CLASS_ID.equals(resource.getValue().getClassId())) {
//                AccessControlRule newRule = AccessControlRule.fromResource(resource);
//
//                // TODO Deal with the effects of changed authorizations correctly
//                for (Resource newlyAuthorizedResource : applyAuthorization(null, workspace, oldAuthorization, authorization, tx)) {
//                    if (!snapshots.keySet().contains(newlyAuthorizedResource.getId())) {
//                        resources.add(newlyAuthorizedResource);
//                    }
//                }
            }
        }

        return resources;
    }

    private boolean outOfTime() {
        return ApiProxy.getCurrentEnvironment().getRemainingMillis() < TIME_LIMIT_MILLISECONDS;
    }
//
//
//    private static Collection<Resource> applyAuthorization(AuthenticatedUser authenticatedUser, WorkspaceEntityGroup workspace,
//                                                           Optional<Authorization> oldAuthorization,
//                                                           Authorization newAuthorization, ReadTx tx) {
//        if (newAuthorization.canViewNowButNotAsOf(oldAuthorization)) {
//            ResourceId resourceId = newAuthorization.getResourceId();
//
//            if (resourceId != null) {
//                final Collection<Resource> result = Lists.newArrayList();
//
//                LatestVersionKey parentKey = new LatestVersionKey(workspace, resourceId);
//
//                for (LatestVersionKey element : descendIdTree(authenticatedUser, parentKey, newAuthorization, tx)) {
//                    result.add(tx.getOrThrow(element).toResource());
//                }
//
//                return result;
//            }
//        }
//
//        return Collections.emptySet();
//    }
//
//    // This method recursively descends the ID tree of a resource node, including only nodes with the same authorization
//    private static List<LatestVersionKey> descendIdTree(AuthenticatedUser authenticatedUser, LatestVersionKey parentKey, Authorization parentAuth, ReadTx tx) {
//        final List<LatestVersionKey> result = Lists.newArrayList(parentKey);
//
//        if (parentAuth != null && parentKey.getResourceId() != null && tx != null) {
//            ResourceId authorizationId = parentAuth.getId();
//
//            if (authorizationId != null) {
//                for (LatestVersionKey child : tx.query(LatestVersion.queryChildKeys(parentKey))) {
//                    Authorization childAuth = new Authorization(parentKey.getWorkspace(), authenticatedUser, child.getResourceId(), tx);
//                    if (authorizationId.equals(childAuth.getId())) {
//                        result.addAll(descendIdTree(authenticatedUser, child, parentAuth, tx));
//                    }
//                }
//            }
//        }
//        return result;
//    }

}
