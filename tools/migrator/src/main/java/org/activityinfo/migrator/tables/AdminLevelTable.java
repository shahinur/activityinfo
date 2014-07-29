package org.activityinfo.migrator.tables;

import org.activityinfo.model.form.*;

import static org.activityinfo.model.shared.CuidAdapter.*;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.shared.CuidAdapter.resourceId;

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

        ResourceId parentLevelId = resourceId(ADMIN_LEVEL_DOMAIN, rs.getInt("ParentId"));
        if(parentLevelId != null) {
            formClass.addField(ADMIN_PARENT_FIELD)
            .setLabel(rs.getString("ParentLevelName"))
            .setType(FormFieldType.REFERENCE)
            .setRange(parentLevelId)
            .setRequired(true)
            .setCardinality(FormFieldCardinality.SINGLE);
        }

        formClass.addField(NAME_FIELD)
            .setLabel("Name")
            .setType(FormFieldType.FREE_TEXT)
            .setRequired(true);

        formClass.addField(CODE_FIELD)
            .setLabel("Code")
            .setType(FormFieldType.FREE_TEXT)
            .setRequired(false);

        return formClass.asResource();
    }
    
}
