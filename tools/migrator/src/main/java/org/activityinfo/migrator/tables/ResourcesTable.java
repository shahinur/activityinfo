package org.activityinfo.migrator.tables;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static org.activityinfo.model.legacy.CuidAdapter.*;

/**
 * Created by alex on 10/16/14.
 */
public class ResourcesTable extends ResourceMigrator {

    private static final Logger LOGGER = Logger.getLogger(ResourcesTable.class.getName());

    private ObjectMapper objectMapper = ObjectMapperFactory.get();

    private MigrationContext context;

    public ResourcesTable(MigrationContext context) {
        this.context = context;
    }

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {

        if(!resourceTablePresent(connection)) {
            return;
        }

        String sql = "select * from resource_version" +
                " where  " + context.filter().resourceFilter() +
                " and version > ? " +
                " order by version";

        if(context.getMaxSnapshotCount() < Integer.MAX_VALUE) {
            sql += " limit " + context.getMaxSnapshotCount();
        }
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setLong(1, context.getSourceVersionMigrated());

        ResultSet resultSet = statement.executeQuery();

        while(resultSet.next()) {

            LOGGER.info("Migrating " + resultSet.getString("id")  + " version " + resultSet.getLong("version"));

            Date commitDate = resultSet.getDate("commit_time");
            long version = resultSet.getLong("version");

            ResourceId resourceId = ResourceId.valueOf(resultSet.getString("id"));
            ResourceId ownerId = ResourceId.valueOf(resultSet.getString("owner_id"));

            Resource resource = Resources.createResource();
            resource.setId(resourceId);
            resource.setOwnerId(ownerId);
            resource.setValue(parseRecord(resultSet.getString("content")));

            // Map BRAC data BACK to legacy ids
            resource = rewriteIds(resource);

            writer.writeResource(getUser(resultSet).getId(), resource, commitDate, null, version);
        }
    }

    private boolean resourceTablePresent(Connection connection) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        try( ResultSet tables = dbm.getTables(null, null, "resource_version", null)) {

            return tables.next();
        }
    }



    private Resource rewriteIds(Resource resource) {
        if(resource.getClassId().equals(FormClass.CLASS_ID)) {
            FormClass formClass = FormClass.fromResource(resource);

            int activityId = CuidAdapter.getLegacyId(formClass.getId());
            ResourceId formClassId = context.getIdStrategy().mapToLegacyId(ACTIVITY_DOMAIN, formClass.getId());
            ResourceId ownerId = context.getIdStrategy().mapToLegacyId(DATABASE_DOMAIN, formClass.getOwnerId());

            FormClass reformClass = new FormClass(formClassId);
            reformClass.setLabel(formClass.getLabel());
            reformClass.setOwnerId(ownerId);
            reformClass.setDescription(formClass.getDescription());

            for(FormField field : formClass.getFields()) {
                FormField reField = new FormField(revertFieldId(field));
                reField.setLabel(field.getLabel());
                reField.setType(rewriteIds(field.getType()));
                reField.setCode(field.getCode());
                reField.setDefaultValue(field.getDefaultValue());
                reField.setPrimaryKey(field.isPrimaryKey());
                reField.setRelevanceConditionExpression(field.getRelevanceConditionExpression());
                reField.setSuperProperties(field.getSuperProperties());
                reField.setVisible(field.isVisible());
                reformClass.addElement(reField);
            }
            System.out.println();
            return reformClass.asResource();

        } else {
            FormInstance instance = FormInstance.fromResource(resource);
            ResourceId siteId = context.getIdStrategy().mapToLegacyId(SITE_DOMAIN, instance.getId());
            ResourceId classId = context.getIdStrategy().mapToLegacyId(ACTIVITY_DOMAIN, instance.getClassId());

            FormInstance rewritten = new FormInstance(siteId, classId);
            for (Map.Entry<ResourceId, FieldValue> entry : instance.getFieldValueMap().entrySet()) {
                ResourceId legacyFieldId = context.getIdStrategy().mapToLegacyId(entry.getKey());
                FieldValue fieldValue = rewriteFieldValue(entry.getValue());
                rewritten.set(legacyFieldId, fieldValue);
            }
            return rewritten.asResource();
        }
    }

    private FieldValue rewriteFieldValue(FieldValue value) {
        if(value instanceof EnumFieldValue) {
            EnumFieldValue enumValue = (EnumFieldValue) value;
            Set<ResourceId> legacyEnumIds = Sets.newHashSet();
            for(ResourceId id : enumValue.getResourceIds()) {
                legacyEnumIds.add(context.getIdStrategy().mapToLegacyId(id));
            }
            return new EnumFieldValue(legacyEnumIds);
        } else {
            return value;
        }
    }

    private FieldType rewriteIds(FieldType type) {
        if(type instanceof EnumType) {
            EnumType enumType = (EnumType)type;
            List<EnumValue> enumValues = Lists.newArrayList();
            for(EnumValue item : enumType.getValues()) {
                ResourceId legacyId = context.getIdStrategy().mapToLegacyId(CuidAdapter.ATTRIBUTE_DOMAIN, item.getId());
                enumValues.add(new EnumValue(legacyId, item.getLabel()));
            }
            return new EnumType(enumType.getCardinality(), enumValues);
        } else {
            return type;
        }
    }

    private ResourceId revertFieldId(FormField field) {
        if(field.getType() instanceof EnumType) {
            return context.getIdStrategy().mapToLegacyId(CuidAdapter.ATTRIBUTE_GROUP_FIELD_DOMAIN, field.getId());
        } else {
            return context.getIdStrategy().mapToLegacyId(CuidAdapter.INDICATOR_DOMAIN, field.getId());
        }
    }

    @VisibleForTesting
    Record parseRecord(String json) throws IOException {
        Resource resource = objectMapper.readValue(json, Resource.class);
        return resource.getValue();
    }

    private AuthenticatedUser getUser(ResultSet resultSet) throws SQLException {
        String userId = resultSet.getString("user_id");
        if(!userId.startsWith("U")) {
            throw new IllegalStateException("Unexpected user id " + userId);
        }
        return new AuthenticatedUser(Integer.parseInt(userId.substring(1)));
    }


}
