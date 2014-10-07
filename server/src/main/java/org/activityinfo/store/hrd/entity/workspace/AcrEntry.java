package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Function;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.expr.ExprValue;
import org.activityinfo.store.hrd.tx.IsEntity;
import org.activityinfo.store.hrd.tx.ListQuery;

/**
 * Datastore entity that stores Access Control Rules
 */
public class AcrEntry implements IsEntity {

    public static final String VIEW_PROPERTY = "view";
    public static final String EDIT_PROPERTY = "edit";
    public static final String OWNER_PROPERTY = "owner";

    private final AcrEntryKey key;
    private boolean owner;
    private String viewCondition;
    private String editCondition;

    public AcrEntry(AcrEntryKey key, Entity entity) {
        this.key = key;
        this.owner = (Boolean)entity.getProperty(OWNER_PROPERTY);
        this.viewCondition = (String) entity.getProperty(VIEW_PROPERTY);
        this.editCondition = (String) entity.getProperty(EDIT_PROPERTY);
    }

    public AcrEntry(Entity entity) {
        this(new AcrEntryKey(entity.getKey()), entity);
    }

    public AcrEntry(WorkspaceEntityGroup workspace, ResourceId id, AuthenticatedUser user) {
        this.key = new AcrEntryKey(new LatestVersionKey(workspace, id), user.getUserResourceId());
    }

    public AcrEntry(WorkspaceEntityGroup workspace, AccessControlRule rule) {
        this.key = new AcrEntryKey(new LatestVersionKey(workspace, rule.getResourceId()), rule.getPrincipalId());
        this.owner = rule.isOwner();
        if(!this.owner) {
            this.editCondition = rule.getEditCondition().getExpression();
            this.viewCondition = rule.getViewCondition().getExpression();
        }
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public String getViewCondition() {
        return viewCondition;
    }

    public void setViewCondition(String viewCondition) {
        this.viewCondition = viewCondition;
    }

    public String getEditCondition() {
        return editCondition;
    }

    public void setEditCondition(String editCondition) {
        this.editCondition = editCondition;
    }

    public AccessControlRule toAccessControlRule() {
        AccessControlRule rule = new AccessControlRule(key.getParent().getResourceId(), key.getPrincipalId());
        rule.setOwner(owner);
        rule.setViewCondition(ExprValue.valueOf(viewCondition));
        rule.setEditCondition(ExprValue.valueOf(editCondition));
        return rule;
    }

    @Override
    public Entity toEntity() {
        Entity entity = new Entity(key.unwrap());
        entity.setUnindexedProperty(OWNER_PROPERTY, owner);
        entity.setUnindexedProperty(VIEW_PROPERTY, viewCondition);
        entity.setUnindexedProperty(EDIT_PROPERTY, editCondition);
        return entity;
    }

    public static ListQuery<Resource> forResource(LatestVersionKey latestVersion) {
        return new ListQuery<>(new Query(AcrEntryKey.KIND, latestVersion.unwrap()), new Function<Entity, Resource>() {

            @Override
            public Resource apply(Entity input) {
                return new AcrEntry(input).toAccessControlRule().asResource();
            }
        });
    }
}
