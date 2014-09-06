package org.activityinfo.migrator.tables;

import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.legacy.CuidAdapter.USER_DOMAIN;
import static org.activityinfo.model.legacy.CuidAdapter.resourceId;

public class UserLoginTable extends SimpleTableMigrator {


    public UserLoginTable(MigrationContext context) {
        super(context);
    }

    @Override
    protected Resource toResource(ResultSet rs) throws SQLException {
        return Resources.createResource()
        .setId(context.resourceId(USER_DOMAIN, rs.getInt("UserId")))
        .setOwnerId(Resources.USER_DATABASE_ID)
        .set("email", rs.getString("email"))
        .set("password", rs.getString("password"))
        .set("locale", rs.getString("locale"))
        .set("name", rs.getString("name"))
        .set("organization", rs.getString("organization"))
        .set("jobTitle", rs.getString("jobtitle"))
        .set("invitedBy", invitedByRef(rs));
    }

    private ResourceId invitedByRef(ResultSet rs) throws SQLException {
        int invitedById = rs.getInt("invitedBy");
        if(rs.wasNull()) {
            return null;
        } else {
            return context.resourceId(USER_DOMAIN, invitedById);
        }
    }
}
