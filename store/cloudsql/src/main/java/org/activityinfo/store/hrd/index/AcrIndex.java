package org.activityinfo.store.hrd.index;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.expr.ExprValue;
import org.activityinfo.store.hrd.entity.LatestContent;
import org.activityinfo.store.hrd.entity.WorkspaceTransaction;

/**
 * Entity which stores resourceId, subjectId, permissions in an entity
 * within the entity group to which the rule applies for fast lookup
 */
public class AcrIndex {

    private static final String KIND = "ACR";

    private final Key workspaceRootKey;

    public AcrIndex(Key workspaceRootKey) {
        this.workspaceRootKey = workspaceRootKey;
    }

    public Key key(AccessControlRule rule) {
        return key(rule.getResourceId(), rule.getPrincipalId());
    }

    public Key key(ResourceId resourceId, ResourceId principalId) {
        Key parentKey = parentKey(resourceId);
        return KeyFactory.createKey(parentKey, KIND, principalId.asString());
    }

    /**
     * The parent key of all ACRs for a given {@code resourceId}
     */
    private Key parentKey(ResourceId resourceId) {
        return KeyFactory.createKey(workspaceRootKey, LatestContent.KIND, resourceId.asString());
    }

    /**
     * Creates or updates an Access Control Rule
     */
    public void put(WorkspaceTransaction tx, AccessControlRule rule) {
        Entity entity = new Entity(key(rule));
        entity.setUnindexedProperty("owner", rule.isOwner());
        entity.setUnindexedProperty("view", toString(rule.getViewCondition()));
        entity.setUnindexedProperty("edit", toString(rule.getEditCondition()));

        tx.put(entity);
        tx.getWorkspace().createResource(tx, rule.asResource());
    }

    public AccessControlRule get(WorkspaceTransaction tx,
                                        ResourceId resourceId,
                                        ResourceId principalId) {

        try {
            Entity entity = tx.get(key(resourceId, principalId));
            return fromEntity(entity);

        } catch (EntityNotFoundException e) {
            AccessControlRule rule = new AccessControlRule(resourceId, principalId);
            rule.setOwner(false);
            rule.setEditCondition(null);
            rule.setViewCondition(null);
            return rule;
        }
    }

    private static AccessControlRule fromEntity(Entity entity) {
        String principalId = entity.getKey().getName();
        String resourceId = entity.getKey().getParent().getName();

        AccessControlRule rule = new AccessControlRule(ResourceId.valueOf(resourceId), ResourceId.valueOf(principalId));
        rule.setOwner((Boolean) entity.getProperty("owner"));
        rule.setViewCondition(ExprValue.valueOf((String) entity.getProperty("view")));
        rule.setEditCondition(ExprValue.valueOf((String) entity.getProperty("edit")));
        return rule;
    }

    /**
     * Retrieves the ACR for a given resource for the current user if one exists
     */
    public Optional<AccessControlRule> getRule(WorkspaceTransaction tx, ResourceId resourceId) {
        try {
            return Optional.of(fromEntity(tx.get(key(resourceId, tx.getUser().getUserResourceId()))));
        } catch (EntityNotFoundException e) {
            return Optional.absent();
        }
    }

    /**
     * Retrieves all ACR IDs for a given resource
     */
    public Iterable<ResourceId> queryRules(WorkspaceTransaction tx, ResourceId resourceId) {
        Query query = new Query(KIND, parentKey(resourceId)).setKeysOnly();
        return Iterables.transform(tx.prepare(query).asIterable(), new Function<Entity, ResourceId>() {
            @Override
            public ResourceId apply(Entity input) {
                Key key = input.getKey();
                String principalId = key.getName();
                String resourceId = key.getParent().getName();
                return AccessControlRule.calculateId(resourceId, principalId);
            }
        });
    }

    private static Object toString(ExprValue condition) {
        if(condition == null) {
            return null;
        } else {
            return condition.getExpression();
        }
    }
}
