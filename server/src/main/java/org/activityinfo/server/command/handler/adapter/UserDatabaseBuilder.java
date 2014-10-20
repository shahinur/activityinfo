package org.activityinfo.server.command.handler.adapter;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.activityinfo.legacy.shared.model.*;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.auth.UserPermission;
import org.activityinfo.model.auth.UserPermissionClass;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.store.StoreReader;

import static org.activityinfo.model.legacy.CuidAdapter.*;

/**
 * Created by alex on 10/15/14.
 */
public class UserDatabaseBuilder {

    private final AuthenticatedUser user;
    private final StoreReader reader;
    private final ResourceNode workspace;
    private final Record databaseRecord;
    private final UserDatabaseDTO db = new UserDatabaseDTO();
    private final CountryDTO country;

    public UserDatabaseBuilder(AuthenticatedUser user, StoreReader reader,
                               CountryProvider countryProvider,
                               ResourceNode workspace) {
        this.user = user;
        this.reader = reader;
        this.workspace = workspace;
        this.databaseRecord = reader.getResource(workspace.getId()).getResource().getValue();
        this.db.setId(getLegacyId(workspace.getId()));
        this.db.setName(workspace.getLabel());
        this.country = countryProvider.getCountry(getCountryId());
        this.db.setCountry(country);

        queryAccessRules();
        try {
            queryFormClasses(workspace);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void queryFormClasses(ResourceNode parent) throws Exception {
        for (ResourceNode child : reader.getFolderItems(parent.getId())) {
            if(child.getClassId().equals(FormClass.CLASS_ID)) {
                addForm(parent, child);
            } else if(child.getClassId().equals(FolderClass.CLASS_ID)) {
                queryFormClasses(child);
            }
        }
    }

    private void addForm(ResourceNode parent, ResourceNode child) throws Exception {
        switch(child.getId().getDomain()) {
            case ACTIVITY_DOMAIN:
                addActivityForm(parent, child);
                break;
            case PARTNER_FORM_CLASS_DOMAIN:
                addPartners(child);
                break;
            case PROJECT_DOMAIN:
                addProject(child);
                break;
        }

    }

    private void addProject(ResourceNode child) {

        Resource resource = reader.getResource(child.getId()).getResource();

        ProjectDTO project = new ProjectDTO();
        project.setId(getLegacyId(child.getId()));
        project.setName(child.getLabel());
        project.setDescription(getDescription(resource));
        project.setUserDatabase(db);
        db.getProjects().add(project);
    }

    private void addPartners(ResourceNode partnerFormClass) throws Exception {
        ResourceId formId = partnerFormClass.getId();
        ResourceCursor cursor = reader.openCursor(partnerFormClass.getId());
        while(cursor.hasNext()) {

            Resource resource = cursor.next();

            PartnerDTO partner = new PartnerDTO();
            partner.setId(getLegacyId(resource.getId()));
            partner.setName(resource.getValue().getString(field(formId, NAME_FIELD).asString()));
            partner.setFullName(resource.getValue().isString(field(formId, FULL_NAME_FIELD).asString()));

            db.getPartners().add(partner);
        }
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
        activity.setDatabase(db);
        activity.setId(getLegacyId(formClass.getId()));
        activity.setName(formClass.getLabel());
        activity.setPublished(0);
        activity.setReportingFrequency(ActivityDTO.REPORT_ONCE);
        activity.setPartnerRange(db.getPartners());

        if(!parent.getId().equals(workspace.getId())) {
            activity.setCategory(parent.getLabel());
        }

        int sortOrder = 1;
        for(FormField field : formClass.getFields()) {
            if(field.getId().equals(field(formClass.getId(), LOCATION_FIELD))) {
                LocationTypeDTO locationType = locationTypeFrom(field);
                if (locationType != null) {
                    activity.setLocationType(locationType);
                }
            } else if(isAttributeGroup(field)) {
                EnumType type = (EnumType) field.getType();
                AttributeGroupDTO group = new AttributeGroupDTO();
                group.setId(getLegacyId(field.getId()));
                group.setName(field.getLabel());
                group.setSortOrder(sortOrder++);
                group.setMandatory(field.isRequired());
                group.setMultipleAllowed(type.getCardinality() == Cardinality.MULTIPLE);

                for(EnumValue item : type.getValues()) {
                    AttributeDTO attribute = new AttributeDTO();
                    attribute.setId(getLegacyId(item.getId()));
                    attribute.setName(item.getLabel());
                    group.getAttributes().add(attribute);
                }

                activity.getAttributeGroups().add(group);
                activity.getFields().add(group);

            } else {
                IndicatorDTO indicator = new IndicatorDTO();
                indicator.setId(getLegacyId(field.getId()));
                indicator.setName(field.getLabel());
                indicator.setMandatory(field.isRequired());
                indicator.setCode(field.getCode());
                indicator.setListHeader(field.getListHeader());
                indicator.setDescription(field.getDescription());
                indicator.setType(field.getType().getTypeClass());

                // requires a value to be set
                indicator.setAggregation(IndicatorDTO.AGGREGATE_SUM);

                if(field.getType() instanceof QuantityType) {
                    QuantityType type = (QuantityType) field.getType();
                    indicator.setUnits(type.getUnits());
                    switch(type.getAggregation()) {
                        case MEAN:
                            indicator.setAggregation(IndicatorDTO.AGGREGATE_AVG);
                            break;
                        case COUNT:
                            indicator.setAggregation(IndicatorDTO.AGGREGATE_SITE_COUNT);
                        case SUM:
                            indicator.setAggregation(IndicatorDTO.AGGREGATE_SUM);
                            break;
                    }
                }

                indicator.setSortOrder(sortOrder++);
                activity.getIndicators().add(indicator);
                activity.getFields().add(indicator);
            }
        }

        // The existing code base expects all activities to have
        // a location type. If this form doesn't have one
        // (there's no reason a 3.0 form should)
        // then we can leverage a 2.0-era hack to keep the code
        // base happy until the hardcoded references to location have
        // been updated
        if(activity.getLocationType() == null) {
            activity.setLocationType(nullLocationType());
        }

        db.getActivities().add(activity);
    }


    private LocationTypeDTO locationTypeFrom(FormField field) {
        if (field.getType() instanceof ReferenceType) {
            ReferenceType type = (ReferenceType) field.getType();
            if (type.getRange().size() == 1) {
                ResourceId formClassId = Iterables.getOnlyElement(type.getRange());
                if(formClassId.getDomain() == LOCATION_TYPE_DOMAIN) {
                    return country.getLocationTypeById(getLegacyId(formClassId));
                }
            }
        }
        return null;
    }

    private LocationTypeDTO nullLocationType() {
        LocationTypeDTO locationType = new LocationTypeDTO();
        locationType.setId(1);
        locationType.setName(LocationTypeDTO.NULL_LOCATION_TYPE_NAME);
        locationType.setAdminLevels(Lists.<AdminLevelDTO>newArrayList());
        return locationType;
    }

    private boolean isAttributeGroup(FormField field) {
        return field.getType() instanceof EnumType && field.getId().getDomain() == ATTRIBUTE_GROUP_FIELD_DOMAIN;
    }

    private boolean isIndicator(FieldType type) {
        return type instanceof TextType ||
                type instanceof QuantityType ||
                type instanceof BarcodeType;
    }

    public UserDatabaseDTO build() {
        return db;
    }


    public ResourceId getCountryId() {
        return databaseRecord.getTagReference(ApplicationProperties.WITHIN);
    }
}
