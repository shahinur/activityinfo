package org.activityinfo.migrator.tables;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.ReferenceValue;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class UserDatabaseTable extends SimpleTableMigrator {

    @Override
    protected Resource toResource(ResultSet rs) throws SQLException {
        ResourceId id = resourceId(DATABASE_DOMAIN, rs.getInt("DatabaseId"));
        ResourceId ownerId = resourceId(USER_DOMAIN, rs.getInt("OwnerUserId"));

        return new FormInstance(id, FolderClass.CLASS_ID)
        .setOwnerId(ownerId)
        .set(FolderClass.LABEL_FIELD_ID, rs.getString("name"))
        .set(FolderClass.DESCRIPTION_FIELD_ID, rs.getString("fullName"))
        .set(CuidAdapter.field(FormClass.CLASS_ID, LOCATION_FIELD),
                new ReferenceValue(resourceId(COUNTRY_DOMAIN, rs.getInt("CountryId"))))
        .asResource();
    }
}
