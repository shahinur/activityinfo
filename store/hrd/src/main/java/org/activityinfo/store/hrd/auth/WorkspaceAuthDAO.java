package org.activityinfo.store.hrd.auth;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.hrd.entity.workspace.*;
import org.activityinfo.store.hrd.tx.ReadableTx;

import java.util.Map;

import static org.activityinfo.model.resource.Resources.ROOT_ID;

/**
 * Data access object for workspace ACRs
 */
public class WorkspaceAuthDAO implements Authorizer {

    private ReadableTx tx;
    private WorkspaceEntityGroup workspace;
    private AuthenticatedUser user;
    private final Optional<AcrEntry> workspaceAcr;
    private Map<ResourceId, Optional<AcrEntry>> rules = Maps.newHashMap();

    public WorkspaceAuthDAO(WorkspaceEntityGroup workspace, AuthenticatedUser user, ReadableTx tx) {
        this.tx = tx;
        this.workspace = workspace;
        this.user = user;

        // Retrieve the top level acr if it exists
        workspaceAcr = findEffectiveRule(workspace.getWorkspaceId());
    }

    private boolean isWorkspaceOwner() {
        return workspaceAcr.isPresent() && workspaceAcr.get().isOwner();
    }

    @Override
    public Authorization forResource(ResourceId resourceId) {
        if(isWorkspaceOwner()) {
            return new IsOwner();
        } else {
            Optional<AcrEntry> rule = findEffectiveRule(resourceId);
            if(!rule.isPresent()) {
                return new NoAuthorization();
            } else if(rule.get().isOwner()) {
                return new IsOwner();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    private Optional<AcrEntry> getRule(ResourceId resourceId) {
        if(rules.containsKey(resourceId)) {
            return rules.get(resourceId);
        } else {
            Optional<AcrEntry> rule = tx.getIfExists(new AcrEntryKey(workspace, user));
            rules.put(resourceId, rule);
            return rule;
        }
    }

    private Optional<AcrEntry> findEffectiveRule(ResourceId resourceId) {

        ResourceId parentId = resourceId;

        while(!ROOT_ID.equals(parentId)) {
            Optional<AcrEntry> rule = getRule(parentId);
            if (rule.isPresent()) {
                return rule;
            }

            if(parentId.equals(workspace.getWorkspaceId())) {
                break;
            }

            // ACRs are inherited from the owner, so if we don't find an ACR here,
            // ascend to this resource's owner in search of an applicable rule.
            Optional<LatestVersion> latestVersion = tx.getIfExists(new LatestVersionKey(workspace, parentId));
            if(!latestVersion.isPresent()) {
                throw new IllegalStateException("Resource " + resourceId + " has non existent ancestor " + parentId);
            }

            parentId = latestVersion.get().getOwnerId();
        }
        return Optional.absent();
    }
}
