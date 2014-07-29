package org.activityinfo.migrator.tables;

import org.activityinfo.model.resource.Reference;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.resource.Resource;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.shared.CuidAdapter.*;

public class UserDatabaseTable extends SimpleTableMigrator {

    @Override
    protected Resource toResource(ResultSet rs) throws SQLException {
        return Resources.createResource()
        .setId(resourceId(DATABASE_DOMAIN, rs.getInt("DatabaseId")))
        .setOwnerId(resourceId(USER_DOMAIN, rs.getInt("OwnerUserId")))
        .set(CLASS_FIELD, Reference.to("_folder"))
        .set(NAME_FIELD, rs.getString("name"))
        .set(LOCATION_FIELD, Reference.to(resourceId(COUNTRY_DOMAIN, rs.getInt("CountryId"))));
    }
}
