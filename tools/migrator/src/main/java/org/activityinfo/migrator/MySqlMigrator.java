package org.activityinfo.migrator;


import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.activityinfo.migrator.filter.DatabaseFilter;
import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.migrator.tables.*;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Migrates data from the legacy MySQL table structure
 */
public class MySqlMigrator {

    private static final Logger LOGGER = Logger.getLogger(MySqlMigrator.class.getName());

    private List<ResourceMigrator> migrators = Lists.newArrayList();

    public MySqlMigrator(MigrationContext context) {
        migrators.add(new UserDatabaseTable(context));
        migrators.add(new Geodatabase(context));
        migrators.add(new CountryTable(context));
        migrators.add(new AdminLevelTable(context));
        migrators.add(new AdminEntityTable(context));
        migrators.add(new LocationTypeTable(context));
        migrators.add(new LocationTable(context));
    //    migrators.add(new UserLoginTable());
        migrators.add(new PartnerFormClass(context));
        migrators.add(new PartnerTable(context));
        migrators.add(new ProjectTable(context));
        migrators.add(new ActivityTable(context));
        migrators.add(new SiteTable(context));
    }

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        try(Connection connection = DriverManager.getConnection(
                "jdbc:mysql://" + args[0] + ":3306/activityinfo?zeroDateTimeBehavior=convertToNull", args[1],
                args.length < 3 ? "" : args[2])) {

            MigrationContext context = new MigrationContext(new DatabaseFilter(1350, 317));
            context.setGeoDbOwnerId(context.getIdStrategy().geoDbId());
            context.setRootId(Resources.ROOT_ID);

            new MySqlMigrator(context).migrate(connection);
        }
    }

    public void migrate(Connection connection) throws Exception {

        ResourceWriter writer = createWriter(connection);
        writer.beginResources();
        for(final ResourceMigrator migrator : migrators) {
            LOGGER.info("Running " + migrator.getClass().getSimpleName() + " migrator...");
            try {
                migrator.getResources(connection, writer);
            } catch(Exception e) {
                throw new RuntimeException("Exception thrown while running migrator " + migrator.getClass().getSimpleName(), e);
            }
        }
        writer.endResources();

        writer.writeUserIndex(queryUserIndex(connection));

        writer.close();
    }

    private Multimap<ResourceId, ResourceId> queryUserIndex(Connection connection) throws SQLException {

        String sql = "SELECT ownerUserId, databaseId FROM userdatabase db WHERE db.dateDeleted IS NULL " +
                     "UNION " +
                     "SELECT up.userId, up.databaseId FROM userpermission up " +
                         "LEFT JOIN userdatabase db ON (up.databaseId=db.DatabaseId) " +
                         "WHERE allowView=1 AND db.dateDeleted is null";


        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql)) {

            Multimap<ResourceId, ResourceId> index = HashMultimap.create();

            while(rs.next()) {
                int userId = rs.getInt(1);
                int databaseId = rs.getInt(2);

                index.put(CuidAdapter.userId(userId), CuidAdapter.databaseId(databaseId));
            }
            return index;
        }
    }

    private ResourceWriter createWriter(Connection connection) throws SQLException, IOException {
        if(!Strings.isNullOrEmpty(System.getProperty("dumpFile"))) {
            File dumpFile = new File(System.getProperty("dumpFile"));
            return new JsonTestUnitWriter(dumpFile);

        } else {
            return new MySqlResourceWriter(connection);
        }
    }

    public void migrate(Connection connection, ResourceWriter writer) throws Exception {
        writer.beginResources();
        for(ResourceMigrator migrator : migrators) {
            LOGGER.log(Level.INFO, "Running migrator " + migrator.getClass().getSimpleName());
            migrator.getResources(connection, writer);
        }
        writer.endResources();
        writer.close();
    }
}
