package org.activityinfo.model.auth;

import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.expr.ExprFieldType;
import org.activityinfo.model.type.expr.ExprValue;

/**
 * A system-level resource which describes a rule that is used to determine
 * the access of a <strong>principal</strong> to a <strong>resource</strong>.
 */
public class AccessControlRule implements IsResource {

    public static final ResourceId CLASS_ID = ResourceId.valueOf("_acr");


    private ResourceId id;

    private ResourceId resourceId;

    private ResourceId principalId;

    private boolean owner;

    private ExprValue viewCondition;

    private ExprValue editCondition;


    public AccessControlRule(ResourceId resourceId, ResourceId principalId) {
        this.id = ResourceId.valueOf("_acr:" + resourceId.asString() + ":" + principalId);
        this.resourceId = resourceId;
        this.principalId = principalId;
    }


    public ResourceId getId() {
        return id;
    }

    /**
     *
     * @return true if the principal identified by {@code principalId} owns the resource
     * identified by {@code resourceId} and its descendants.
     */
    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    /**
     *
     * @return a reference to the user or user group to whom the rule grant access.
     */
    public ResourceId getPrincipalId() {
        return principalId;
    }

    /**
     *
     * @return the id of the resource to which this rule applies.
     */
    public ResourceId getResourceId() {
        return resourceId;
    }

    public void setResourceId(ResourceId resourceId) {
        this.resourceId = resourceId;
    }


    /**
     *
     * @return the boolean expression that determines whether the principal is authorized
     * too view a given resource.
     */
    public ExprValue getViewCondition() {
        return viewCondition;
    }

    public void setViewCondition(ExprValue viewCondition) {
        this.viewCondition = viewCondition;
    }

    /**
     *
     * @return the boolean expression that determines whether the principal is authorized
     * to modify a given resource, or create new resources which are owned by this resource.
     */
    public ExprValue getEditCondition() {
        return editCondition;
    }

    public void setEditCondition(ExprValue editCondition) {
        this.editCondition = editCondition;
    }

    @Override
    public Resource asResource() {
        Resource resource = Resources.createResource();
        resource.setId(id);
        resource.setOwnerId(resourceId);
        resource.set("classId", CLASS_ID.asString());
        resource.set("principal", new ReferenceValue(principalId).asRecord());
        if(owner) {
            resource.set("owner", true);
        } else {
            resource.set("owner", false);
            resource.set("view", viewCondition.asRecord());
            resource.set("edit", editCondition.asRecord());
        }
        return resource;
    }

    public static AccessControlRule fromResource(Resource resource) {
        ResourceId resourceId = resource.getOwnerId();
        ReferenceValue principal = ReferenceValue.fromRecord(resource.getRecord("principal"));

        AccessControlRule rule = new AccessControlRule(resourceId, principal.getResourceId());
        rule.setResourceId(resource.getOwnerId());
        rule.setOwner(resource.getBoolean("owner"));
        rule.setViewCondition(ExprFieldType.TYPE_CLASS.deserialize(resource.getRecord("view")));
        rule.setEditCondition(ExprFieldType.TYPE_CLASS.deserialize(resource.getRecord("edit")));
        return rule;
    }
}
