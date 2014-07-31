package org.activityinfo.migrator;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormElement;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormSection;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.time.LocalDateType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class ActivityMigrator extends ResourceMigrator {

    private final String NAME_FIELD_NAME = "label";

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
                "LEFT JOIN locationtype L on (A.locationtypeid=L.locationtypeid)";

        Map<Integer, List<FormField>> attributes = queryAttributeGroups(connection);
        Map<Integer, List<FormElement>> indicators = queryIndicators(connection);

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
                        writer.write(categoryResource(databaseResourceId, categoryId, category));
                        ownerId = categoryId;
                    }

                    writeSiteForm(ownerId, rs, indicators.get(activityId), attributes.get(activityId), writer);
                }
            }
        }
    }

    private Resource categoryResource(ResourceId databaseId, ResourceId categoryId, String category) {
        return Resources.createResource()
                        .setId(categoryId)
                        .setOwnerId(databaseId)
                        .set(CLASS_FIELD, "_folder")
                        .set(NAME_FIELD_NAME, category);
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

                    // todo
                    Cardinality cardinality = rs.getBoolean("multipleAllowed") ?
                            Cardinality.MULTIPLE : Cardinality.SINGLE;

                    List<EnumValue> values = Lists.newArrayList();
                    // TODO(alex) add attributes
//                    for(AttributeDTO attribute : getAttributes()) {
//                        values.add(new EnumValue(CuidAdapter.attributeId(attribute.getId()), attribute.getName()));
//                    }

                    fields.add(new FormField(CuidAdapter.attributeGroupField(rs.getInt("attributeGroupId")))
                            .setLabel(rs.getString("name"))
                            .setType(new EnumType(cardinality, values))
                            .setRequired(rs.getBoolean("mandatory")));
                }
            }
        }
        return activityMap;
    }

    private FormField indicatorField(ResultSet rs) throws SQLException {
        FormField field = new FormField(
                CuidAdapter.indicatorField(rs.getInt("IndicatorId")))
                .setLabel(rs.getString("Name"))
                .setRequired(rs.getInt("Mandatory") == 1)
                .setDescription(rs.getString("Description"))
                .setExpression(Strings.emptyToNull(rs.getString("Expression")));

        switch (rs.getString("Type")) {
            default:
            case "QUANTITY":
                field.setType(new QuantityType().setUnits(rs.getString("units")));
                break;
        }
        return field;
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
                               List<FormField> attributes,
                               ResourceWriter writer) throws SQLException, IOException {


        int activityId = rs.getInt("activityId");
        int databaseId = rs.getInt("databaseId");
        ResourceId classId = CuidAdapter.activityFormClass(activityId);

        FormClass siteForm = new FormClass(classId);
        siteForm.setLabel(rs.getString("name"));
        siteForm.setParentId(ownerId);

        FormField partnerField = new FormField(CuidAdapter.field(classId, CuidAdapter.PARTNER_FIELD))
                .setLabel("Partner")
                .setType(ReferenceType.single(CuidAdapter.partnerFormClass(databaseId)))
                .setRequired(true);
        siteForm.addElement(partnerField);

        FormField projectField = new FormField(CuidAdapter.field(classId, CuidAdapter.PROJECT_FIELD))
                .setLabel("Project")
                .setType(ReferenceType.single(CuidAdapter.projectFormClass(databaseId)));
        siteForm.addElement(projectField);

        FormField startDateField = new FormField(CuidAdapter.field(classId, CuidAdapter.START_DATE_FIELD))
                .setLabel("Start Date")  // TODO i18n
                .setType(LocalDateType.INSTANCE)
                .setRequired(true);
        siteForm.addElement(startDateField);

        FormField endDateField = new FormField(CuidAdapter.field(classId, CuidAdapter.END_DATE_FIELD))
                .setLabel("End Date")  // TODO i18N
                .setType(LocalDateType.INSTANCE)
                .setRequired(true);
        siteForm.addElement(endDateField);

        FormField locationField = new FormField(CuidAdapter.locationField(activityId))
                .setLabel(rs.getString("locationTypeName"))
                .setType(ReferenceType.single(locationRange(rs)))
                .setRequired(true);
        siteForm.addElement(locationField);


        if(attributes != null) {
            siteForm.getElements().addAll(attributes);
        }
        if(indicators != null) {
            siteForm.getElements().addAll(indicators);
        }

        FormField commentsField = new FormField(CuidAdapter.commentsField(activityId));
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
