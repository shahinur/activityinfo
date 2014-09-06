package org.activityinfo.migrator.tables;

import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.migrator.tables.Geodatabase.COUNTRY_FORM_CLASS_ID;
import static org.activityinfo.model.legacy.CuidAdapter.*;

public class CountryTable extends SimpleTableMigrator {

    public CountryTable(MigrationContext context) {
        super(context);
    }

    @Override
    protected String query() {
        return "select * from country WHERE " + filter.countryFilter();
    }

    @Override
    protected void writeFormClass() {
        super.writeFormClass();
    }

    @Override
    public Resource toResource(ResultSet rs) throws SQLException {
        ResourceId id = context.resourceId(COUNTRY_DOMAIN, rs.getInt("CountryId"));
        return new FormInstance(id, context.getIdStrategy().countryFormClassId())
        .set(field(COUNTRY_FORM_CLASS_ID, CODE_FIELD), rs.getString("iso2"))
        .set(field(COUNTRY_FORM_CLASS_ID, NAME_FIELD), rs.getString("Name"))
        .asResource();

        // TODO : geometry;
    }
}
