package org.activityinfo.migrator.tables;

import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.system.FolderClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

import static org.activityinfo.model.legacy.CuidAdapter.COUNTRY_DOMAIN;
import static org.activityinfo.model.legacy.CuidAdapter.DATABASE_DOMAIN;

public class UserDatabaseTable extends ResourceMigrator {

    private static final Logger LOGGER = Logger.getLogger(UserDatabaseTable.class.getName());

    private final MigrationContext context;

    public UserDatabaseTable(MigrationContext context) {
        this.context = context;
    }

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {
        String sql = "select * from userdatabase where datedeleted is null AND " + context.filter().databaseFilter("databaseId");
        try(Statement stmt = connection.createStatement()) {
            try(ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()) {
                    writeDatabase(writer, rs);
                }
            }
        }
    }


    private void writeDatabase(ResourceWriter writer, ResultSet rs) throws Exception {

        ResourceId id = context.resourceId(DATABASE_DOMAIN, rs.getInt("DatabaseId"));

        Record database = Records.builder(FolderClass.CLASS_ID)
        .set(FolderClass.LABEL_FIELD_NAME, rs.getString("name"))
        .set(FolderClass.DESCRIPTION_FIELD_NAME, rs.getString("fullName"))
        .setTag(ApplicationProperties.WITHIN, context.resourceId(COUNTRY_DOMAIN, rs.getInt("CountryId")))
        .build();

        Resource resource = Resources.createResource();
        resource.setId(id);
        resource.setOwnerId(Resources.ROOT_ID);
        resource.setValue(database);

        writer.writeResource(rs.getInt("ownerUserId"), resource, null, null);

        LOGGER.info("Database " + id);


//        ResourceId ownerId = resourceId(USER_DOMAIN, rs.getInt("OwnerUserId"));
//        AccessControlRule rule = new AccessControlRule(database.getId(), ownerId);
//        rule.setOwner(true);
//
//        writer.writeResource(rule.asResource(), null, null);
    }
}
