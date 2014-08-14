package org.activityinfo.migrator;


import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.activityinfo.migrator.tables.*;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;

/**
 * Migrates data from the legacy MySQL table structure
 */
public class MySqlMigrator {


    private List<ResourceMigrator> migrators = Lists.newArrayList();

    public MySqlMigrator() {
        migrators.add(new Geodatabase());
        migrators.add(new CountryTable());
        migrators.add(new AdminLevelTable());
        migrators.add(new AdminEntityTable());
        migrators.add(new LocationTypeTable());
        migrators.add(new LocationTable());
        migrators.add(new UserLoginTable());
        migrators.add(new UserDatabaseTable());
        migrators.add(new PartnerFormClass());
        migrators.add(new PartnerTable());
        migrators.add(new ProjectTable());
        migrators.add(new ActivityTable());
        migrators.add(new SiteTable());
    }

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        try(Connection connection = DriverManager.getConnection(
                "jdbc:mysql://" + args[0] + ":3306/activityinfo?zeroDateTimeBehavior=convertToNull", args[1],
                args.length < 3 ? "" : args[2])) {

            new MySqlMigrator().migrate(connection);
        }

    }

    public void migrate(Connection connection) throws Exception {

        ResourceWriter writer = createWriter(connection);
        writer.beginResources();
        for(final ResourceMigrator migrator : migrators) {
            System.out.println("Running " + migrator.getClass().getSimpleName() + " migrator...");
            migrator.getResources(connection, writer);
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
            return new MySqlResourceDumpWriter(dumpFile);

        } else {
            return new MySqlResourceWriter(connection);
        }
    }

    public void migrate(Connection connection, ResourceWriter writer) throws Exception {
        for(ResourceMigrator migrator : migrators) {
            migrator.getResources(connection, writer);
        }
    }
}
