package org.activityinfo.model.auth;

import org.activityinfo.model.annotation.RecordBean;
import org.activityinfo.model.annotation.Reference;
import org.activityinfo.model.annotation.Transient;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.FolderClass;

/**
 * Version 2.x style authorization record -- only at workspace level
 */
@RecordBean(classId = "_userPermission")
public class UserPermission {


    private ResourceId workspaceId;
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

    UserPermission() {
    }

    public UserPermission(ResourceId workspaceId, ResourceId principalId) {
        this.workspaceId = workspaceId;
        this.principalId = principalId;
    }

    public static ResourceId calculateId(ResourceId workspaceId, ResourceId principalId) {
        return ResourceId.valueOf(workspaceId.asString() + principalId.asString());
    }

    @Transient
    public ResourceId getId() {
        return calculateId(workspaceId, principalId);
    }

    /**
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
     * @return a reference to the user or user group to whom the rule grant access.
     */
    @Reference(range = FormClass.class)
    public ResourceId getPrincipalId() {
        return principalId;
    }

    @Reference(range = FolderClass.class)
    public ResourceId getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(ResourceId workspaceId) {
        this.workspaceId = workspaceId;
    }

    public void setPrincipalId(ResourceId principalId) {
        this.principalId = principalId;
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

    @Reference(range = FormClass.class)
    public ResourceId getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(ResourceId userGroup) {
        this.userGroup = userGroup;
    }
}
