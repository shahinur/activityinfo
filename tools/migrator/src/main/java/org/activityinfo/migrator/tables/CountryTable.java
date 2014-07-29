package org.activityinfo.migrator.tables;

import org.activityinfo.model.resource.Resources;
import org.activityinfo.migrator.globals.GeographicDatabase;
import org.activityinfo.migrator.resource.Geometry;
import org.activityinfo.model.resource.Resource;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.shared.CuidAdapter.*;

public class CountryTable extends SimpleTableMigrator {


    @Override
    public Resource toResource(ResultSet rs) throws SQLException {
        return Resources.createResource()
        .setId(resourceId(COUNTRY_DOMAIN, rs.getInt("CountryId")))
        .setOwnerId(GeographicDatabase.RESOURCE_ID)
        .set(CODE_FIELD, rs.getString("iso2"))
        .set(NAME_FIELD, rs.getString("Name"))
        .set(GEOMETRY_FIELD, Geometry.from(rs));
    }
}
