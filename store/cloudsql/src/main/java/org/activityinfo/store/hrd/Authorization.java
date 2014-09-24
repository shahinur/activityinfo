package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.type.expr.ExprValue;
import org.activityinfo.store.hrd.entity.WorkspaceTransaction;
import org.activityinfo.store.hrd.index.AcrIndex;

import javax.ws.rs.WebApplicationException;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.activityinfo.model.resource.Resources.ROOT_ID;
import static org.activityinfo.store.hrd.entity.Content.deserializeResourceNode;
import static org.activityinfo.store.hrd.entity.Workspace.ROOT_KIND;

public class Authorization {

    private WorkspaceTransaction transaction;
    private ResourceId userResourceId;

    final private AccessControlRule accessControlRule;

    /**
     * Standard constructor. Extracts one {@link AccessControlRule} corresponding to a user and resource from the index.
     * @param authenticatedUser The {@link AuthenticatedUser} this {@link AccessControlRule} should correspond with.
     * @param resourceId The id of the {@link Resource} this {@link AccessControlRule} should correspond with.
     * @param transaction The {@link WorkspaceTransaction} that should be used to extract the {@link AccessControlRule}.
     */
    public Authorization(AuthenticatedUser authenticatedUser, ResourceId resourceId, WorkspaceTransaction transaction) {
        Preconditions.checkNotNull(authenticatedUser);
        Preconditions.checkNotNull(resourceId);
        Preconditions.checkNotNull(transaction);

        this.userResourceId = Preconditions.checkNotNull(authenticatedUser.getUserResourceId());
        this.transaction = transaction;

        this.accessControlRule = findRule(transaction, resourceId);
    }

    public Authorization(AccessControlRule accessControlRule) {
        this.accessControlRule = accessControlRule;
    }

    private AccessControlRule findRule(WorkspaceTransaction transaction, ResourceId resourceId) {

        while(!ROOT_ID.equals(resourceId)) {
            Optional<AccessControlRule> rule = AcrIndex.getRule(transaction, resourceId, userResourceId);
            if(rule.isPresent()) {
                return rule.get();
            }

            // ACRs are inherited from the owner, so if we don't find an ACR here,
            // ascend to this resource's owner in search of an applicable rule.
            try {
                resourceId = transaction.getWorkspace().getLatestContent(resourceId).getAsNode(transaction).getOwnerId();
            } catch (EntityNotFoundException e) {
                throw new IllegalStateException("Missing resource/owner: " + resourceId);
            }
        }
        return null;
    }

    /**
     * Special-purpose constructor. Won't fetch ACRs recursively and should only be used on root entities (workspaces).
     * @param authenticatedUser The {@link AuthenticatedUser} this {@link AccessControlRule} should correspond with.
     * @param resourceId The id of the {@link Resource} this {@link AccessControlRule} should correspond with.
     * @param datastore The {@link DatastoreService} that should be used to extract the {@link AccessControlRule}.
     */
    public Authorization(AuthenticatedUser authenticatedUser, ResourceId resourceId, DatastoreService datastore) {
        ResourceId userResourceId = Preconditions.checkNotNull(authenticatedUser.getUserResourceId());
        Optional<AccessControlRule> rule = AcrIndex.getRule(datastore, resourceId, userResourceId);

        try {
            Entity entity = datastore.get(KeyFactory.createKey(ROOT_KIND, resourceId.asString()));
            ResourceNode resourceNode = deserializeResourceNode(entity);
            assert ROOT_ID.equals(resourceNode.getOwnerId());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("Missing resource: " + resourceId);
        }

        if(rule.isPresent()) {
            accessControlRule = rule.get();
        } else {
            accessControlRule = null;
        }
    }

    /**
     * Special-purpose constructor. Checks that the ACR (as a {@link Resource}) refers to the {@link AuthenticatedUser}.
     * @param authenticatedUser The {@link AuthenticatedUser} this {@link AccessControlRule} should correspond with.
     * @param rule The requested {@link AccessControlRule}, represented as a {@link Resource}.
     */
    Authorization(AuthenticatedUser authenticatedUser, Resource rule) {
        if (authenticatedUser != null && rule != null) {
            AccessControlRule accessControlRule = AccessControlRule.fromResource(rule);
            ResourceId userResourceId = authenticatedUser.getUserResourceId();
            assert userResourceId != null;

            if (userResourceId.equals(accessControlRule.getPrincipalId())) {
                this.accessControlRule = accessControlRule;
                return;
            }
        }

        accessControlRule = null;
    }

    /**
     * @return whether the user can view the resource
     */
    public boolean canView() {
        return isOwner() || evaluate(getViewCondition());
    }

    /**
     * @return whether the user can edit the resource
     */
    public boolean canEdit() {
        return isOwner() || evaluate(getEditCondition());
    }

    /**
     * @return the id of the {@link AccessControlRule}
     */
    public ResourceId getId() {
        return accessControlRule != null ? accessControlRule.getId() : null;
    }

    /**
     * @return the id of the resource to which the {@link AccessControlRule} applies
     */
    public ResourceId getResourceId() {
        return accessControlRule != null ? accessControlRule.getResourceId() : null;
    }

    /**
     * This method is determines if a change in authorization has made a resource newly visible.
     * @param oldAuthorization the old authorization object to compare this one to
     * @return whether the user can now view the resource, but could not view it previously
     */
    public boolean canViewNowButNotAsOf(Optional<Authorization> oldAuthorization) {
        if (oldAuthorization.isPresent()) {
            return canView() && !oldAuthorization.get().canView();
        } else {
            return false;   // If the object was newly created, its previous visibility is undefined, so nothing changed
        }
    }

    public void assertCanEdit() {
        if(!canEdit()) {
            throw new WebApplicationException(UNAUTHORIZED);
        }
    }

    public void assertCanView() {
        if (!canView()) {
            throw new WebApplicationException(UNAUTHORIZED);
        }
    }


    private static boolean evaluate(ExprValue exprValue) {
        return exprValue != null && "true".equals(exprValue.getExpression());
    }

    private ExprValue getEditCondition() {
        return accessControlRule != null ? accessControlRule.getEditCondition() : null;
    }

    private ExprValue getViewCondition() {
        return accessControlRule != null ? accessControlRule.getViewCondition() : null;
    }

    public boolean isOwner() {
        return accessControlRule != null && accessControlRule.isOwner();
    }

    public Authorization ofChild(ResourceId childId) {
        Preconditions.checkNotNull(userResourceId);
        Preconditions.checkNotNull(transaction);

        Optional<AccessControlRule> rule = AcrIndex.getRule(transaction, childId, userResourceId);
        if(rule.isPresent()) {
            return new Authorization(rule.get());
        } else {
            return this; // if child doesn't have ACR return parent ACR
        }
    }
}