package org.activityinfo.store.hrd.index;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.expr.ExprValue;
import org.activityinfo.store.hrd.entity.ResourceGroup;
import org.activityinfo.store.hrd.entity.VersionedTransaction;

/**
 * Entity which stores resourceId, subjectId, permissions in an entity
 * within the entity group to which the rule applies for fast lookup
 */
public class AcrIndex {

    private static final String KIND = "ACR";

    public Key key(AccessControlRule rule) {
        return key(rule.getResourceId(), rule.getPrincipalId());
    }

    public static Key key(ResourceId resourceId, ResourceId principalId) {
        return KeyFactory.createKey(parentKey(resourceId), KIND, principalId.asString());
    }

    private static Key parentKey(ResourceId resourceId) {
        return new ResourceGroup(resourceId).getKey();
    }

    public Entity put(AccessControlRule rule) {
        Entity entity = new Entity(key(rule.getResourceId(), rule.getPrincipalId()));
        entity.setUnindexedProperty("owner", rule.isOwner());
        entity.setUnindexedProperty("view", toString(rule.getViewCondition()));
        entity.setUnindexedProperty("edit", toString(rule.getEditCondition()));
        return entity;
    }

    public static AccessControlRule get(VersionedTransaction versionedTransaction,
                                        ResourceId resourceId,
                                        ResourceId principalId) {

        try {
            Entity entity = versionedTransaction.get(key(resourceId, principalId));
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
        rule.setOwner((Boolean)entity.getProperty("owner"));
        rule.setViewCondition(ExprValue.valueOf((String) entity.getProperty("view")));
        rule.setEditCondition(ExprValue.valueOf((String) entity.getProperty("edit")));
        return rule;
    }

    public static Iterable<Resource> queryRules(DatastoreService datastore, ResourceId resourceId) {
        Query query = new Query(KIND, parentKey(resourceId));
        return Iterables.transform(datastore.prepare(query).asIterable(), new Function<Entity, Resource>() {
            @Override
            public Resource apply(Entity input) {
                return fromEntity(input).asResource();
            }
        });
    }

    private Object toString(ExprValue viewCondition) {
        if(viewCondition == null) {
            return null;
        } else {
            return viewCondition.getExpression();
        }
    }
}
