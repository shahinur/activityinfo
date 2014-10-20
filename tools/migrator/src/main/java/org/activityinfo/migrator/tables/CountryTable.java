package org.activityinfo.migrator.tables;

import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.activityinfo.model.legacy.CuidAdapter.COUNTRY_DOMAIN;

public class CountryTable extends ResourceMigrator {

    public static final int GEO_ADMIN_USER_ID = 9999;
    private MigrationContext context;

    public CountryTable(MigrationContext context) {
        this.context = context;
    }

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {
        String sql = "select * from country WHERE " + context.filter().countryFilter();
        try(Statement stmt = connection.createStatement()) {
            try(ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()) {
                    writeCountry(rs, writer);
                }
            }
        }
    }


    public void writeCountry(ResultSet rs, ResourceWriter writer) throws Exception {
        ResourceId id = context.resourceId(COUNTRY_DOMAIN, rs.getInt("CountryId"));
        Resource workspace = new FormInstance(id, FolderClass.CLASS_ID)
                .setOwnerId(Resources.ROOT_ID)
                        //.set(FolderClass.LABEL_FIELD_ID, rs.getString("iso2"))
                .set(FolderClass.LABEL_FIELD_ID, rs.getString("Name"))
                .asResource();

        writer.writeResource(GEO_ADMIN_USER_ID, workspace, null, null, 0);
      //  writer.writeResource(GEO_ADMIN_USER_ID, acr.asResource(), null, null);
    }

}
