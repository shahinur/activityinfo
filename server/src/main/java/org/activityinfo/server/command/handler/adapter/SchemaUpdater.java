package org.activityinfo.server.command.handler.adapter;

import org.activityinfo.legacy.shared.command.CreateEntity;
import org.activityinfo.legacy.shared.command.UpdateEntity;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.model.IndicatorDTO;
import org.activityinfo.legacy.shared.model.UserDatabaseDTO;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.legacy.KeyGenerator;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.TypeRegistry;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.number.AggregationType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.service.store.ResourceStore;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class SchemaUpdater {

    public static final int RDC_COUNTRY_ID = 1;
    private ResourceStore store;
    private AuthenticatedUser user;
    private KeyGenerator keyGenerator = new KeyGenerator();

    public SchemaUpdater(ResourceStore store, AuthenticatedUser user) {
        this.store = store;
        this.user = user;
    }

    public int create(CreateEntity cmd) {
        PropertyMap properties = new PropertyMap(cmd.getProperties());

        switch(cmd.getEntityName()) {
            case "UserDatabase":
                return createDatabase(properties);
            case "Activity":
                return createActivity(properties);
            case "LocationType":
                return createLocationType(properties);

            case "AttributeGroup":
            case "Attribute":
            case "Indicator":
                return updateActivity(properties).execute(cmd);

            default:
                throw new CommandException("Invalid entity class " + cmd.getEntityName());
        }
    }

    public void update(UpdateEntity cmd) {
        PropertyMap properties = new PropertyMap(cmd.getChanges());
        switch(cmd.getEntityName()) {
            case "UserDatabase":
                updateDatabase(cmd.getId(), properties);
                break;

            case "Activity":
                updateActivity(properties);
                break;

            case "Project":
                updateProject(cmd.getId(), properties);
                break;

            case "AttributeGroup":
            case "Attribute":
            case "Indicator":
                updateActivity(properties).execute(cmd);
                break;

            default:
                throw new CommandException("Invalid entity class " + cmd.getEntityName());
        }
    }

    private void updateProject(int id, PropertyMap properties) {
        ResourceId resourceId = CuidAdapter.resourceId(PROJECT_DOMAIN, id);
        FormInstance instance = FormInstance.fromResource(getResource(resourceId));
        ResourceId classId = instance.getClassId();
        if(properties.contains("name")) {
            instance.set(field(classId, NAME_FIELD), properties.getString("name"));
        }
        if(properties.contains("description")) {
            instance.set(field(classId, FULL_NAME_FIELD), properties.getString("description"));
        }
        store.put(user, instance.asResource());
    }


    private int createDatabase(PropertyMap properties) {

        int databaseId = generateId();

        Record record = Records.builder(FolderClass.CLASS_ID)
                .set(FolderClass.LABEL_FIELD_NAME, properties.getString(UserDatabaseDTO.NAME_PROPERTY))
                .set(FolderClass.DESCRIPTION_FIELD_NAME, properties.getString(UserDatabaseDTO.FULL_NAME_PROPERTY))
                .setTag(ApplicationProperties.WITHIN, country(properties))
                .build();

        Resource workspace = Resources.createResource();
        workspace.setId(CuidAdapter.databaseId(databaseId));
        workspace.setOwnerId(Resources.ROOT_ID);
        workspace.setValue(record);
        store.create(user, workspace);

        return databaseId;
    }

    private ResourceId country(PropertyMap properties) {
        if(properties.contains("countryId")) {
            return CuidAdapter.resourceId(COUNTRY_DOMAIN, properties.getInt("countryId"));
        } else {
            // Historically RDC was the default country
            return CuidAdapter.resourceId(COUNTRY_DOMAIN, RDC_COUNTRY_ID);
        }
    }

    private void updateDatabase(int databaseId, PropertyMap changes) {

        Resource resource = getResource(databaseId(databaseId));

        RecordBuilder updatedRecord = Records.buildCopyOf(resource.getValue());

        if(changes.contains(UserDatabaseDTO.NAME_PROPERTY)) {
            updatedRecord.set(UserDatabaseDTO.NAME_PROPERTY, changes.getString(UserDatabaseDTO.NAME_PROPERTY));
        }
        if(changes.contains(UserDatabaseDTO.FULL_NAME_PROPERTY)) {
            updatedRecord.set(UserDatabaseDTO.FULL_NAME_PROPERTY, changes.getString(UserDatabaseDTO.FULL_NAME_PROPERTY));
        }

        resource.setValue(updatedRecord.build());

        store.put(user, resource);
    }

    private int createActivity(PropertyMap properties) {
        int databaseId = properties.getInt("databaseId");
        int activityId = generateId();

        FormClass formClass = new FormClass(CuidAdapter.activityFormClass(activityId));
        formClass.setOwnerId(CuidAdapter.databaseId(databaseId));
        formClass.setLabel(properties.getString("name"));

        FormField locationField = new FormField(CuidAdapter.field(formClass.getId(), LOCATION_FIELD));
        FormClass locationType = findLocation(properties.getInt("locationTypeId"));
        locationField.setType(ReferenceType.single(locationType.getId()));
        locationField.setCode("location");
        locationField.setLabel(locationType.getLabel());
        locationField.setRequired(true);
        formClass.addElement(locationField);

        store.create(user, formClass.asResource());

        return activityId;
    }

    private FormClass findLocation(int locationTypeId) {
        return FormClass.fromResource(store.get(user, CuidAdapter.locationFormClass(locationTypeId)).getResource());
    }

    private int createLocationType(PropertyMap properties) {

        int databaseId = properties.getInt("databaseId");
        int locationTypeId = generateId();

        // create the entity
        ResourceId formClassId = CuidAdapter.locationFormClass(locationTypeId);
        FormClass formClass = new FormClass(formClassId);
        formClass.setOwnerId(CuidAdapter.databaseId(databaseId));
        formClass.setLabel(properties.getString("name"));

        formClass.addField(field(formClassId, NAME_FIELD))
                .setLabel("Name")
                .setRequired(true)
                .setSuperProperty(ApplicationProperties.LABEL_PROPERTY)
                .setType(TextType.INSTANCE);

        formClass.addField(field(formClassId, AXE_FIELD))
                .setLabel("Alternate Name")
                .setRequired(false)
                .setType(TextType.INSTANCE);


//        if(adminLevelsByCountry.containsKey(countryId)) {
//            formClass.addField(field(formClassId, ADMIN_FIELD))
//                    .setLabel("Administrative Unit")
//                    .setRequired(false)
//                    .setType(ReferenceType.multiple(adminLevelsByCountry.get(countryId)));
//        }

        formClass.addField(field(formClassId, GEOMETRY_FIELD))
                .setLabel("Geographic Position")
                .setRequired(false)
                .setType(GeoPointType.INSTANCE);


        store.put(user, formClass.asResource());

        return locationTypeId;
    }


    private ActivityUpdater updateActivity(PropertyMap properties) {
        // Indicators, attributes groups, and attributes are now
        // all part of the form class
        int activityId = properties.getInt("activityId");

        UserResource resource = store.get(user, CuidAdapter.activityFormClass(activityId));
        return new ActivityUpdater(FormClass.fromResource(resource.getResource()));
    }

    private class ActivityUpdater {

        private final FormClass formClass;
        private final int activityId;

        private ActivityUpdater(FormClass formClass) {
            this.formClass = formClass;
            this.activityId = CuidAdapter.getLegacyId(formClass.getId());
        }

        public int execute(CreateEntity entity) {
            switch(entity.getEntityName()) {
                case "Indicator":
                    return createIndicator(new PropertyMap(entity.getProperties()));
            }
            throw new UnsupportedOperationException();
        }

        private int createIndicator(PropertyMap propertyMap) {
            int indicatorId = KeyGenerator.get().generateInt();
            FormField field = new FormField(CuidAdapter.indicatorField(indicatorId));
            updateIndicatorField(propertyMap, field);
            formClass.addElement(field);
            store.put(user, formClass.asResource());
            return indicatorId;
        }

        private void updateIndicatorField(PropertyMap map, FormField field) {
            if (map.contains("name")) {
                field.setLabel(map.getString("name"));
            }
            if (map.contains("nameInExpression")) {
                field.setCode(map.getString("nameInExpression"));
            }
            if (map.contains("description")) {
                field.setDescription(map.getString("description"));
            }
            if (map.contains("mandatory")) {
                field.setRequired(map.getBoolean("mandatory"));
            }

            FieldType fieldType = field.getType();
            if (map.contains("type")) {
                fieldType = TypeRegistry.get().getTypeClass(map.getString("type")).createType();
            }
            if (map.contains("units")) {
                if(fieldType instanceof QuantityType) {
                    ((QuantityType) fieldType).setUnits(map.getString("units"));
                }
            }
            if (map.contains("aggregation")) {
                if(fieldType instanceof QuantityType) {
                    ((QuantityType) fieldType).setAggregation(aggregation(map.getInt("aggregation")));
                }
            }
            boolean ignoreCalculation = false;
            if (map.contains("calculatedAutomatically")) {
               if(!map.getBoolean("calculatedAutomatically")) {
                   ignoreCalculation = true;
               }
            }
            if (map.contains("expression") && !ignoreCalculation) {
                if(fieldType instanceof CalculatedFieldType) {
                    ((CalculatedFieldType) fieldType).setExpression(map.getString("expression"));
                } else {
                    fieldType = new CalculatedFieldType(map.getString("expression"));
                }
            }

            if (map.contains("listHeader")) {
                field.setListHeader(map.getString("listHeader"));
            }

//
//            if (changes.containsKey("category")) {
//                indicator.setCategory(trim(changes.get("category")));
//            }
//
//
//            if (changes.containsKey("sortOrder")) {
//                indicator.setSortOrder((Integer) changes.get("sortOrder"));
//            }

        }

        private AggregationType aggregation(int aggregation) {
            switch(aggregation) {
                default:
                case IndicatorDTO.AGGREGATE_SUM:
                    return AggregationType.SUM;
                case IndicatorDTO.AGGREGATE_AVG:
                    return AggregationType.MEAN;
                case IndicatorDTO.AGGREGATE_SITE_COUNT:
                    return AggregationType.COUNT;
            }
        }

        public void execute(UpdateEntity cmd) {
            throw new UnsupportedOperationException();
        }

        public void updateActivityProperties(PropertyMap map) {
            if (map.contains("locationTypeId")) {
                updateLocationField(map.getInt("locationTypeId"));
            }

            if (map.contains("locationType")) {
                PropertyMap location = map.getModel("locationType");
                updateLocationField(location.getInt("id"));
            }

//            if (map.contains("category")) {
//                String category = Strings.nullToEmpty((String) map.get("category")).trim();
//                activity.setCategory(Strings.emptyToNull(category));
//            }

//            if (map.contains("reportingFrequency")) {
//                activity.setReportingFrequency((Integer) map.get("reportingFrequency"));
//            }
//
//            if (map.contains("published")) {
//                activity.setPublished((Integer) map.get("published"));
//            }
//
//            if (map.contains("sortOrder")) {
//                activity.setSortOrder((Integer) map.get("sortOrder"));
//            }

        }

        private void updateLocationField(int locationTypeId) {
            throw new UnsupportedOperationException();
        }

        public FormField findLocationField() {
            for(FormField field : formClass.getFields()) {
                if(field.getId().equals(CuidAdapter.locationField(activityId))) {
                    return field;
                }
            }
            return null;
        }

    }


    private Resource getResource(ResourceId resourceId) {
        return store.get(user, resourceId).getResource();
    }


    private int generateId() {
        return keyGenerator.generateInt();
    }
}
