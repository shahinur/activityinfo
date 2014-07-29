package org.activityinfo.migrator.tables;

import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.resource.Resource;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.shared.CuidAdapter.*;

public class AdminEntityTable extends SimpleTableMigrator {

    @Override
    protected Resource toResource(ResultSet rs) throws SQLException {
        return Resources.createResource()
        .setId(resourceId(ADMIN_ENTITY_DOMAIN, rs.getInt("AdminEntityId")))
        .setOwnerId(resourceId(ADMIN_LEVEL_DOMAIN, rs.getInt("AdminLevelId")))
        .set(CLASS_FIELD, resourceId(ADMIN_LEVEL_DOMAIN, rs.getInt("AdminLevelId")))
        .set(NAME_FIELD, rs.getString("name"))
        .set(ADMIN_PARENT_FIELD, resourceId(ADMIN_ENTITY_DOMAIN, rs.getInt("AdminEntityParentId")))
        .set(CODE_FIELD, rs.getString("Code"));
        // TODO:.set(GEOMETRY_FIELD, Geometry.from(rs));
    }
}
