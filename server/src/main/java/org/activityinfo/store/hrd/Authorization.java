package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.expr.ExprValue;
import org.activityinfo.store.hrd.entity.WorkspaceTransaction;
import org.activityinfo.store.hrd.index.AcrIndex;

public class Authorization {
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

        accessControlRule = findRule(transaction, resourceId, authenticatedUser);
    }

    private AccessControlRule findRule(WorkspaceTransaction transaction, ResourceId resourceId, AuthenticatedUser authenticatedUser) {
        ResourceId userResourceId = Preconditions.checkNotNull(authenticatedUser.getUserResourceId());

        while(!resourceId.equals(Resources.ROOT_ID)) {
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

    private static boolean evaluate(ExprValue exprValue) {
        return exprValue != null && "true".equals(exprValue.getExpression());
    }

    private ExprValue getEditCondition() {
        return accessControlRule != null ? accessControlRule.getEditCondition() : null;
    }

    private ExprValue getViewCondition() {
        return accessControlRule != null ? accessControlRule.getViewCondition() : null;
    }

    private boolean isOwner() {
        return accessControlRule != null && accessControlRule.isOwner();
    }
}
