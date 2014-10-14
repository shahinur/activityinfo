package org.activityinfo.model.auth;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.Types;

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
    private boolean edit;
    private boolean editAll;
    private boolean view;
    private boolean viewAll;
    private boolean design;
    private boolean manageUsers;
    private boolean manageAllUsers;
    private ResourceId userGroup;

    public AccessControlRule(ResourceId resourceId, ResourceId principalId) {
        this.id = calculateId(resourceId.asString(), principalId.asString());
        this.resourceId = resourceId;
        this.principalId = principalId;
    }

    public static boolean isAcrId(ResourceId id) {
        return id.asString().startsWith("_acr-");
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

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean isEditAll() {
        return editAll;
    }

    public void setEditAll(boolean editAll) {
        this.editAll = editAll;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public boolean isViewAll() {
        return viewAll;
    }

    public void setViewAll(boolean viewAll) {
        this.viewAll = viewAll;
    }

    public boolean isDesign() {
        return design;
    }

    public void setDesign(boolean design) {
        this.design = design;
    }

    public boolean isManageUsers() {
        return manageUsers;
    }

    public void setManageUsers(boolean manageUsers) {
        this.manageUsers = manageUsers;
    }

    public boolean isManageAllUsers() {
        return manageAllUsers;
    }

    public void setManageAllUsers(boolean manageAllUsers) {
        this.manageAllUsers = manageAllUsers;
    }

    public ResourceId getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(ResourceId userGroup) {
        this.userGroup = userGroup;
    }

    @Override
    public Resource asResource() {
        RecordBuilder record = Records.builder(CLASS_ID);
        record.set("principal", new ReferenceValue(principalId).asRecord());
        if(owner) {
            record.set("owner", true);
        } else {
            record.set("owner", false);
            record.set("view", view);
            record.set("viewAll", viewAll);
            record.set("edit", edit);
            record.set("editAll", editAll);
            record.set("manageUsers", manageUsers);
            record.set("manageAllUsers", manageAllUsers);
            record.set("design", design);
            record.set("userGroup", new ReferenceValue(userGroup));
        }

        Resource resource = Resources.createResource();
        resource.setId(id);
        resource.setOwnerId(resourceId);
        resource.setValue(record.build());

        return resource;
    }

    public static AccessControlRule fromResource(Resource resource) {
        ResourceId resourceId = resource.getOwnerId();
        Record record = resource.getValue();
        ReferenceValue principal = ReferenceValue.fromRecord(record.getRecord("principal"));

        return fromRecord(resourceId, principal.getResourceId(), record);
    }

    public static AccessControlRule fromRecord(ResourceId resourceId, ResourceId principalId, Record record) {
        AccessControlRule rule = new AccessControlRule(resourceId, principalId);
        rule.setResourceId(resourceId);
        if (record.getBoolean("owner")) {
            rule.setOwner(true);
        } else {
            rule.setOwner(false);
            rule.view = record.getBoolean("view", false);
            rule.viewAll = record.getBoolean("viewAll", false);
            rule.edit = record.getBoolean("edit", false);
            rule.editAll = record.getBoolean("editAll", false);
            rule.design = record.getBoolean("design", false);
            rule.manageUsers = record.getBoolean("manageUsers", false);
            rule.manageAllUsers = record.getBoolean("manageAllUsers", false);
            rule.userGroup = Types.readReference(record, "userGroup");
        }
        return rule;
    }

    public static ResourceId calculateId(String resourceId, String principalId) {
        return ResourceId.valueOf("_acr-" + resourceId + "-" + principalId);
    }

    @Override
    public String toString() {
        return "AccessControlRule{" +
                "id=" + id +
                ", resourceId=" + resourceId +
                ", principalId=" + principalId +
                ", owner=" + owner +
                ", edit=" + edit +
                ", editAll=" + editAll +
                ", view=" + view +
                ", viewAll=" + viewAll +
                ", design=" + design +
                ", manageUsers=" + manageUsers +
                ", manageAllUsers=" + manageAllUsers +
                ", userGroup=" + userGroup +
                '}';
    }
}
