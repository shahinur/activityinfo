package org.activityinfo.migrator.tables;

import org.activityinfo.model.form.*;

import static org.activityinfo.model.legacy.CuidAdapter.*;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.primitive.TextType;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.legacy.CuidAdapter.resourceId;

public class AdminLevelTable extends SimpleTableMigrator {

    @Override
    protected String query() {
        return "SELECT L.*, P.Name ParentLevelName FROM adminlevel L " +
               "LEFT JOIN adminlevel P ON (L.parentId = P.AdminLevelId)";
    }

    @Override
    protected Resource toResource(ResultSet rs) throws SQLException {
        ResourceId id = resourceId(ADMIN_LEVEL_DOMAIN, rs.getInt("AdminLevelId"));
        FormClass formClass = new FormClass(id)
        .setOwnerId(resourceId(COUNTRY_DOMAIN, rs.getInt("CountryId")))
        .setLabel(rs.getString("Name"));

        int parentId = rs.getInt("ParentId");
        if(!rs.wasNull()) {
            ResourceId parentLevelId = resourceId(ADMIN_LEVEL_DOMAIN, parentId);
            formClass.addElement(new FormField(field(id, ADMIN_PARENT_FIELD))
                .setLabel(rs.getString("ParentLevelName"))
                .setSuperProperty(ApplicationProperties.PARENT_PROPERTY)
                .setRequired(true)
                .setType(ReferenceType.single(parentLevelId)));
        }

        formClass.addField(field(id, NAME_FIELD))
            .setLabel("Name")
            .setType(TextType.INSTANCE)
            .setSuperProperty(ApplicationProperties.LABEL_PROPERTY)
            .setRequired(true);

        formClass.addField(field(id, CODE_FIELD))
            .setLabel("Code")
            .setType(TextType.INSTANCE)
            .setRequired(false);

        return formClass.asResource();
    }
    
}
