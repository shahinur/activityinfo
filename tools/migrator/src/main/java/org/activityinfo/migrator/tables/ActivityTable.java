package org.activityinfo.migrator.tables;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormElement;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormSection;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class ActivityTable extends ResourceMigrator {


    public static final int ONCE = 0;
    public static final int MONTHLY = 1;

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws SQLException, IOException {

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
                "LEFT JOIN locationtype L on (A.locationtypeid=L.locationtypeid) " +
                "LEFT JOIN userdatabase d on (A.databaseId=d.DatabaseId) " +
                "WHERE d.dateDeleted is null and A.dateDeleted is null ";

        Map<Integer, List<EnumValue>> attributes = queryAttributes(connection);
        Map<Integer, List<FormElement>> fields = queryFields(connection, attributes);
        Set<Integer> databasesWithProjects = queryDatabasesWithProjects(connection);
        Set<ResourceId> categories = Sets.newHashSet();

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
                        ownerId = categoryId;
                        if(!categories.contains(categoryId)) {
                            writer.write(categoryResource(databaseResourceId, categoryId, category));
                            categories.add(categoryId);
                        }
                    }

                    writeSiteForm(ownerId, rs, fields.get(activityId), databasesWithProjects, writer);
                }
            }
        }
    }

    private Set<Integer> queryDatabasesWithProjects(Connection connection) throws SQLException {

        String sql = "SELECT distinct databaseId from project WHERE dateDeleted is null";

        Set<Integer> databases = Sets.newHashSet();

        try(Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    databases.add(rs.getInt(1));
                }
            }
        }
        return databases;
    }

    private Resource categoryResource(ResourceId databaseId, ResourceId categoryId, String category) {
        return Resources.createResource()
                        .setId(categoryId)
                        .setOwnerId(databaseId)
                        .set(CLASS_FIELD, FolderClass.CLASS_ID)
                        .set(FolderClass.LABEL_FIELD_ID.asString(), category);
    }

    private Map<Integer, List<FormElement>> queryFields(
            Connection connection, Map<Integer, List<EnumValue>> attributes) throws SQLException {

        String indicatorQuery = "(SELECT " +
                                        "ActivityId, " +
                                        "IndicatorId as Id, " +
                                        "Category, " +
                                        "Name, " +
                                        "Description, " +
                                        "Mandatory, " +
                                        "Type, " +
                                        "NULL as MultipleAllowed, " +
                                        "units, " +
                                        "SortOrder " +
                                    "FROM indicator " +
                                    "WHERE dateDeleted IS NULL) " +
                                "UNION ALL " +
                                "(SELECT " +
                                        "A.ActivityId, " +
                                        "G.attributeGroupId as Id, " +
                                        "NULL as Category, " +
                                        "Name, " +
                                        "NULL as Description, " +
                                        "Mandatory, " +
                                        "'ENUM' as Type, " +
                                        "multipleAllowed, " +
                                        "NULL as Units, " +
                                        "SortOrder " +
                                    "FROM attributegroup G " +
                                    "INNER JOIN attributegroupinactivity A on G.attributeGroupId = A.attributeGroupId " +
                                    "WHERE dateDeleted is null) " +
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
                        list.add(createField(rs, attributes));

                    } else {
                        FormSection categorySection = findCategorySection(list, category);
                        if(categorySection == null) {
                            categorySection = new FormSection(activityFormSection(activityId, category));
                            categorySection.setLabel(category);
                            list.add(categorySection);
                        }
                        categorySection.addElement(createField(rs, attributes));
                    }
                }
            }
        }
        return activityMap;
    }


    private Map<Integer, List<EnumValue>> queryAttributes(Connection connection) throws SQLException {

        String sql = "SELECT * " +
                     "FROM attribute A " +
                     "WHERE A.dateDeleted is null " +
                     "ORDER BY sortOrder";

        Map<Integer, List<EnumValue>> groupMap = Maps.newHashMap();


        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {
                while(rs.next()) {
                    int attributeGroupId = rs.getInt("AttributeGroupId");

                    List<EnumValue> values = groupMap.get(attributeGroupId);
                    if(values == null) {
                        groupMap.put(attributeGroupId, values = Lists.newArrayList());
                    }

                    int attributeId = rs.getInt("attributeId");
                    String attributeName = rs.getString("name");

                    values.add(new EnumValue(CuidAdapter.attributeId(attributeId), attributeName));
                }
            }
        }
        return groupMap;
    }


    private FormField createField(ResultSet rs, Map<Integer, List<EnumValue>> attributes) throws SQLException {

        ResourceId fieldId;
        if(rs.getString("Type").equals("ENUM")) {
            fieldId = CuidAdapter.attributeGroupField(rs.getInt("id"));
        } else {
            fieldId = CuidAdapter.indicatorField(rs.getInt("id"));
        }

        FormField field = new FormField(fieldId)
                .setLabel(rs.getString("Name"))
                .setRequired(rs.getInt("Mandatory") == 1)
                .setDescription(rs.getString("Description"));

        switch (rs.getString("Type")) {
            default:
            case "QUANTITY":
                field.setType(new QuantityType().setUnits(rs.getString("units")));
                break;
            case "FREE_TEXT":
                field.setType(TextType.INSTANCE);
                break;
            case "NARRATIVE":
                field.setType(TextType.INSTANCE);
                break;
            case "ENUM":
                field.setType(createEnumType(rs, attributes));
                break;
        }
        return field;
    }

    private EnumType createEnumType(ResultSet rs, Map<Integer, List<EnumValue>> attributes) throws SQLException {

        Cardinality cardinality;
        if(rs.getBoolean("multipleAllowed")) {
            cardinality = Cardinality.MULTIPLE;
        } else {
            cardinality = Cardinality.SINGLE;
        }

        List<EnumValue> enumValues = attributes.get(rs.getInt("id"));
        if(enumValues == null) {
            enumValues = Lists.newArrayList();
        }
        return new EnumType(cardinality, enumValues);
    }

    private FormSection findCategorySection(List<FormElement> section, String category) {
        for(FormElement element : section) {
            if(element instanceof FormSection && element.getLabel().equals(category)) {
                return (FormSection) element;
            }
        }
        return null;
    }

    private void writeSiteForm(ResourceId ownerId,
                               ResultSet rs,
                               List<FormElement> indicators,
                               Set<Integer> databasesWithProjects,
                               ResourceWriter writer) throws SQLException, IOException {


        int activityId = rs.getInt("activityId");
        int databaseId = rs.getInt("databaseId");
        ResourceId classId = CuidAdapter.activityFormClass(activityId);

        FormClass siteForm = new FormClass(classId);
        siteForm.setLabel(rs.getString("name"));
        siteForm.setParentId(ownerId);

        FormField partnerField = new FormField(field(classId, PARTNER_FIELD))
                .setLabel("Partner")
                .setType(ReferenceType.single(CuidAdapter.partnerFormClass(databaseId)))
                .setRequired(true);
        siteForm.addElement(partnerField);

        if(databasesWithProjects.contains(databaseId)) {
            FormField projectField = new FormField(field(classId, PROJECT_FIELD))
                .setLabel("Project")
                .setType(ReferenceType.single(CuidAdapter.projectFormClass(databaseId)));
            siteForm.addElement(projectField);
        }

//        FormField dateField = new FormField(DATE_FIELD)
//                .setLabel("Date")
//                .setType(LocalDateIntervalType.INSTANCE)
//                .setRequired(true);
//        siteForm.addElement(dateField);

        siteForm.addElement(
            new FormField(field(classId, START_DATE_FIELD))
                .setLabel("Start Date")
                .setType(LocalDateType.INSTANCE)
                .setRequired(true));

        siteForm.addElement(
            new FormField(field(classId, END_DATE_FIELD))
                .setLabel("End Date")
                .setType(LocalDateType.INSTANCE)
                .setRequired(true));


        FormField locationField = new FormField(field(classId, LOCATION_FIELD))
                .setLabel(rs.getString("locationTypeName"))
                .setType(ReferenceType.single(locationRange(rs)))
                .setRequired(true);
        siteForm.addElement(locationField);


        if(indicators != null) {
            siteForm.getElements().addAll(indicators);
        }

        FormField commentsField = new FormField(field(classId, COMMENT_FIELD));
        commentsField.setType(NarrativeType.INSTANCE);
        commentsField.setLabel("Comments");
        siteForm.addElement(commentsField);

        writer.write(siteForm.asResource());
    }

    private ResourceId locationRange(ResultSet rs) throws SQLException {

        int boundAdminLevelId = rs.getInt("BoundAdminLevelId");
        if(rs.wasNull()) {
            int locationTypeId = rs.getInt("LocationTypeId");
            return CuidAdapter.locationFormClass(locationTypeId);
        } else {
            return CuidAdapter.adminLevelFormClass(boundAdminLevelId);
        }
    }

}
