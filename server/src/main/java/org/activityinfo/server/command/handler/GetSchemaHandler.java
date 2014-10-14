package org.activityinfo.server.command.handler;

import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.model.*;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.auth.UserPermission;
import org.activityinfo.model.auth.UserPermissionClass;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.StoreReader;

import java.util.List;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class GetSchemaHandler implements CommandHandler<GetSchema> {

    private final ResourceStore store;

    @Inject
    public GetSchemaHandler(ResourceStore store) {
        this.store = store;
    }

    @Override
    public CommandResult execute(GetSchema cmd, User userEntity) throws CommandException {
        AuthenticatedUser user = new AuthenticatedUser(userEntity.getId());
        try(StoreReader reader = store.openReader(user)) {

            SchemaDTO schema = new SchemaDTO();

            List<ResourceNode> workspaces = reader.getOwnedOrSharedWorkspaces();
            for(ResourceNode workspace : workspaces) {
                schema.getDatabases().add(new Builder(user, reader, workspace).build());
            }

            return schema;
        }
    }

    private class Builder {

        private AuthenticatedUser user;
        private StoreReader reader;
        private ResourceNode workspace;
        private UserDatabaseDTO db = new UserDatabaseDTO();

        public Builder(AuthenticatedUser user, StoreReader reader, ResourceNode workspace) {
            this.user = user;
            this.reader = reader;
            this.workspace = workspace;
            this.db.setId(getLegacyId(workspace.getId()));
            this.db.setName(workspace.getLabel());

            queryAccessRules();
            queryFormClasses(workspace);
        }

        private void queryFormClasses(ResourceNode parent) {
            for (ResourceNode child : reader.getFolderItems(parent.getId())) {
                if(child.getClassId().equals(FormClass.CLASS_ID)) {
                    addForm(parent, child);
                } else if(child.getClassId().equals(FolderClass.CLASS_ID)) {
                    queryFormClasses(child);
                }
            }
        }

        private void addForm(ResourceNode parent, ResourceNode child) {
            switch(child.getId().getDomain()) {
                case ACTIVITY_DOMAIN:
                    addActivityForm(parent, child);
                    break;
                case PARTNER_FORM_CLASS_DOMAIN:
                    addPartner(child);
                    break;
                case PROJECT_FIELD:
                    addProject(child);
                    break;
            }

        }

        private void addProject(ResourceNode child) {

            Resource resource = reader.getResource(child.getId()).getResource();

            ProjectDTO project = new ProjectDTO();
            project.setId(CuidAdapter.getLegacyId(child.getId()));
            project.setName(child.getLabel());
            project.setDescription(getDescription(resource));
            project.setUserDatabase(db);
            db.getProjects().add(project);
        }

        private void addPartner(ResourceNode child) {
            Resource resource = reader.getResource(child.getId()).getResource();

            PartnerDTO partner = new PartnerDTO();
            partner.setId(child.getId());
            partner.setName(child.getLabel());
            partner.setFullName(getDescription(resource));
            db.getPartners().add(partner);
        }

        private String getDescription(Resource resource) {
            return resource.getValue().isString(field(resource.getClassId(), FULL_NAME_FIELD).asString());
        }

        private void queryAccessRules() {

            if(workspace.isOwner()) {
                db.setAmOwner(true);
                db.setDesignAllowed(true);
                db.setEditAllowed(true);
                db.setEditAllAllowed(true);
                db.setManageUsersAllowed(true);
                db.setManageAllUsersAllowed(true);
            } else {

                UserPermission rule = UserPermissionClass.INSTANCE
                        .toBean(
                                reader.getResource(
                                        UserPermission.calculateId(workspace.getId(), user.getUserResourceId()))
                                        .getResource().getValue());

                db.setAmOwner(rule.isOwner());
                db.setDesignAllowed(rule.isDesign());
                db.setEditAllowed(rule.isEdit());
                db.setEditAllAllowed(rule.isEditAll());
                db.setManageUsersAllowed(rule.isManageUsers());
                db.setManageAllUsersAllowed(rule.isManageAllUsers());
            }
        }

        private void addActivityForm(ResourceNode parent, ResourceNode child) {
            FormClass formClass = FormClass.fromResource(reader.getResource(child.getId()).getResource());

            ActivityDTO activity = new ActivityDTO();
            activity.setId(CuidAdapter.getLegacyId(formClass.getId()));
            activity.setName(formClass.getLabel());
            if(!parent.getId().equals(workspace.getId())) {
                activity.setCategory(parent.getLabel());
            }

            int sortOrder = 1;
            for(FormField field : formClass.getFields()) {
                if(field.getId().equals(field(formClass.getId(), LOCATION_FIELD))) {
                    // TODO
                } else if(isIndicator(field.getType())) {
                    IndicatorDTO dto = new IndicatorDTO();
                    dto.setFieldId(field.getId());
                    dto.setProperties(field.asRecord().asMap());
                    dto.setSortOrder(sortOrder++);
                    activity.getIndicators().add(dto);
                }
            }

            db.getActivities().add(activity);
        }

        private boolean isAttributeGroup(FieldType type) {
            return type instanceof EnumType;
        }

        private boolean isIndicator(FieldType type) {
            return type instanceof TextType ||
                   type instanceof QuantityType ||
                   type instanceof BarcodeType;
        }

        public UserDatabaseDTO build() {
            return db;
        }

    }

}
