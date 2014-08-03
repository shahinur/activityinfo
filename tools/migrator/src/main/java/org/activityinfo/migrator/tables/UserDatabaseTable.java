package org.activityinfo.migrator.tables;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.ReferenceValue;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class UserDatabaseTable extends SimpleTableMigrator {

    @Override
    protected Resource toResource(ResultSet rs) throws SQLException {
        return Resources.createResource()
        .setId(resourceId(DATABASE_DOMAIN, rs.getInt("DatabaseId")))
        .setOwnerId(resourceId(USER_DOMAIN, rs.getInt("OwnerUserId")))
        .set("name", rs.getString("name"))
        .set("location", new ReferenceValue(resourceId(COUNTRY_DOMAIN, rs.getInt("CountryId"))).asRecord());
    }
}
