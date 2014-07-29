package org.activityinfo.migrator.tables;

import com.google.api.client.util.Lists;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.activityinfo.model.form.*;
import org.activityinfo.model.resource.Reference;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.shared.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.migrator.ResourceMigrator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.activityinfo.model.shared.CuidAdapter.*;

public class ActivityTable extends ResourceMigrator {

    public static final int ONCE = 0;
    public static final int MONTHLY = 1;

    @Override
    public Iterable<Resource> getResources(Connection connection) throws SQLException {

        String sql =
            "SELECT " +
               "A.ActivityId, " +
               "A.category, " +
               "A.Name, " +
               "A.ReportingFrequency, " +
               "A.DatabaseId, " +
               "A.LocationTypeId, " +
               "L.Name locationTypeName, " +
               "L.BoundAdminLevelId " +
           "FROM activity A " +
           "LEFT JOIN locationtype L on (A.locationtypeid=L.locationtypeid)";

        Map<Integer, List<FormField>> attributes = queryAttributeGroups(connection);
        Map<Integer, List<FormElement>> indicators = queryIndicators(connection);

        List<Resource> resources = Lists.newArrayList();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {
                    int databaseId = rs.getInt("databaseId");
                    ResourceId databaseResourceId = cuid(DATABASE_DOMAIN, databaseId);

                    int activityId = rs.getInt("activityId");
                    String category = rs.getString("category");

                    ResourceId ownerId;
                    if(Strings.isNullOrEmpty(category)) {
                        ownerId = databaseResourceId;
                    } else {
                        ResourceId categoryId = CuidAdapter.activityCategoryFolderId(databaseId, category);
                        resources.add(categoryResource(databaseResourceId, categoryId, category));
                        ownerId = categoryId;
                    }

                    resources.addAll(
                        siteForm(ownerId, rs,
                            indicators.get(activityId),
                            attributes.get(activityId)));
                }
            }
        }
        return resources;
    }

    private Resource categoryResource(ResourceId databaseId, ResourceId categoryId, String category) {
        return Resources.createResource()
        .setId(categoryId)
        .setOwnerId(databaseId)
        .set(CLASS_FIELD, Reference.to("_folder"))
        .set(NAME_FIELD, category);
    }

    private Map<Integer, List<FormElement>> queryIndicators(Connection connection) throws SQLException {
        String indicatorQuery = "SELECT * FROM indicator " +
                                "WHERE dateDeleted IS NULL " +
                                "ORDER BY SortOrder";

        Map<Integer, List<FormElement>> activityMap = Maps.newHashMap();
        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(indicatorQuery)) {
                while(rs.next()) {
                    int activityId = rs.getInt("ActivityId");
                    List<FormElement> list = activityMap.get(activityId);
                    if(list == null) {
                        activityMap.put(activityId, list = Lists.newArrayList());
                    }

                    String category = rs.getString("Category");
                    if(Strings.isNullOrEmpty(category)) {
                        list.add(indicatorField(rs));

                    } else {
                        FormSection categorySection = findCategorySection(list, category);
                        if(categorySection == null) {
                            categorySection = new FormSection(activityFormSection(activityId, category));
                            categorySection.setLabel(category);
                            list.add(categorySection);
                        }
                        categorySection.addElement(indicatorField(rs));
                    }
                }
            }
        }
        return activityMap;
    }

    private Map<Integer, List<FormField>> queryAttributeGroups(Connection connection) throws SQLException {
        String sql = "SELECT * " +
                     "FROM attributegroup G " +
                     "INNER JOIN attributegroupinactivity A on G.attributeGroupId = A.attributeGroupId " +
                     "ORDER BY sortOrder";

        Map<Integer, List<FormField>> activityMap = Maps.newHashMap();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {
                while(rs.next()) {
                    int activityId = rs.getInt("ActivityId");

                    List<FormField> fields = activityMap.get(activityId);
                    if(fields == null) {
                        activityMap.put(activityId, fields = Lists.newArrayList());
                    }

                    int groupId = rs.getInt("AttributeGroupId");
                    fields.add(new FormField(activityFormClass(activityId), attributeGroupName(groupId))
                                .setLabel(rs.getString("Name"))
                                .setType(FormFieldType.REFERENCE)
                                .setCardinality(cardinality(rs.getInt("multipleAllowed")))
                                .setRequired(rs.getInt("mandatory") == 1)
                                .setRange(attributeGroupFormClass(groupId)));
                }
            }
        }
        return activityMap;
    }

    private FormFieldCardinality cardinality(int multipleAllowed) throws SQLException {
        switch(multipleAllowed) {
            case 0:
                return FormFieldCardinality.SINGLE;
            case 1:
                return FormFieldCardinality.MULTIPLE;
        }
        throw new IllegalArgumentException("value: " + multipleAllowed);
    }

    private FormField indicatorField(ResultSet rs) throws SQLException {
        FormField field = new FormField(
                CuidAdapter.activityFormClass(rs.getInt("ActivityId")),
                CuidAdapter.indicatorFieldName(rs.getInt("IndicatorId")))
        .setLabel(rs.getString("Name"))
        .setRequired(rs.getInt("Mandatory") == 1)
        .setDescription(rs.getString("Description"))
        .setExpression(Strings.emptyToNull(rs.getString("Expression")));

        switch (rs.getString("Type")) {
            default:
            case "QUANTITY":
                field.setType(FormFieldType.QUANTITY);
                field.setUnit(rs.getString("units"));
                break;
        }
        return field;
    }

    private FormSection findCategorySection(List<FormElement> section, String category) {
        for(FormElement element : section) {
            if(element instanceof FormSection && ((FormSection) element).getLabel().equals(category)) {
                return (FormSection) element;
            }
        }
        return null;
    }

    private List<Resource> siteForm(ResourceId ownerId, ResultSet rs,
                                    List<FormElement> indicators,
                                    List<FormField> attributes) throws SQLException {

        FormClass siteForm = new FormClass(cuid(ACTIVITY_DOMAIN, rs.getInt("ActivityId")))
        .setOwnerId(ownerId)
        .setLabel(rs.getString("name"));

        int reportingFrequency = rs.getInt("ReportingFrequency");
        if(reportingFrequency == ONCE) {
            // TODO: convert to date range once we've updated FormField types
            siteForm.addField(START_DATE_FIELD)
                .setLabel("Start Date")
                .setType(FormFieldType.LOCAL_DATE)
                .setRequired(true);

            siteForm.addField(END_DATE_FIELD)
                 .setLabel("End Date")
                 .setType(FormFieldType.LOCAL_DATE)
                 .setRequired(true);
        }

        siteForm.addField(PARTNER_FIELD)
            .setLabel("Partner")
            .setType(FormFieldType.REFERENCE)
            .setRange(partnerFormClass(rs.getInt("databaseId")))
            .setCardinality(FormFieldCardinality.SINGLE);

        siteForm.addField(LOCATION_FIELD)
             .setLabel(rs.getString("LocationTypeName"))
             .setType(FormFieldType.REFERENCE)
             .setRange(locationRange(rs))
             .setCardinality(FormFieldCardinality.SINGLE);

        if(attributes != null) {
            siteForm.getElements().addAll(attributes);
        }

        if(reportingFrequency == ONCE && indicators != null) {
            siteForm.getElements().addAll(indicators);
        }

        siteForm.addField(COMMENT_FIELD)
            .setLabel("Comments")
            .setType(FormFieldType.NARRATIVE);

        if(reportingFrequency == ONCE) {
            return Arrays.asList(siteForm.asResource());

        } else {
            FormClass reportForm = new FormClass(cuid(ACTIVITY_MONTHLY_REPORT, rs.getInt("ActivityId")));
            reportForm.setOwnerId(siteForm.getId());

            reportForm.addField(DATE_FIELD)
              .setLabel("Month")
              .setType(FormFieldType.LOCAL_DATE);

            return Arrays.asList(siteForm.asResource(), reportForm.asResource());
        }
    }

    private ResourceId locationRange(ResultSet rs) throws SQLException {
        ResourceId boundLevelId = resourceId(ADMIN_LEVEL_DOMAIN, rs.getInt("BoundAdminLevelId"));
        if(boundLevelId == null) {
            return resourceId(LOCATION_TYPE_DOMAIN, rs.getInt("LocationTypeId"));
        } else {
            return boundLevelId;
        }
    }
}
