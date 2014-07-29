package org.activityinfo.migrator.tables;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormFieldType;
import org.activityinfo.model.resource.Resource;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.shared.CuidAdapter.*;

public class AttributeGroupTable extends SimpleTableMigrator {

    @Override
    protected String query() {
        return "SELECT G.*, A.activityId activityId " +
               "FROM attributegroup G " +
               "INNER JOIN attributegroupinactivity A ON (G.attributegroupid=A.attributegroupid)";
    }

    @Override
    protected Resource toResource(ResultSet rs) throws SQLException {

        FormClass form = new FormClass(attributeGroupFormClass(rs.getInt("AttributeGroupId")))
        .setOwnerId(resourceId(ACTIVITY_DOMAIN, rs.getInt("activityId")))
        .setLabel(rs.getString("name"));

        form.addField(NAME_FIELD)
        .setLabel("Name")
        .setType(FormFieldType.FREE_TEXT)
        .setRequired(true);

        return form.asResource();
    }


}
