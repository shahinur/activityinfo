package org.activityinfo.migrator.tables;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.shared.CuidAdapter.USER_DOMAIN;
import static org.activityinfo.model.shared.CuidAdapter.resourceId;

public class UserLoginTable extends SimpleTableMigrator {
    @Override
    protected Resource toResource(ResultSet rs) throws SQLException {
        return Resources.createResource()
        .setId(resourceId(USER_DOMAIN, rs.getInt("UserId")))
        .setOwnerId(Resources.USER_DATABASE_ID)
        .set("email", rs.getString("email"))
        .set("password", rs.getString("password"))
        .set("locale", rs.getString("locale"))
        .set("name", rs.getString("name"))
        .set("organization", rs.getString("organization"))
        .set("jobTitle", rs.getString("jobtitle"))
        .set("invitedBy", resourceId(USER_DOMAIN, rs.getInt("invitedBy")));
    }
}
