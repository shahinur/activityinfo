package org.activityinfo.migrator;


import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Migrates data from the legacy MySQL table structure
 */
public class MySqlMigrator {


    private List<ResourceMigrator> migrators = Lists.newArrayList();

    public MySqlMigrator() {
        //        migrators.add(new GeographicDatabase());
        //        migrators.add(new CountryTable());
        //        migrators.add(new AdminLevelTable());
        //        migrators.add(new AdminEntityTable());
        //        migrators.add(new LocationTypeTable());
        //        migrators.add(new LocationTable());
        //        migrators.add(new UserLoginTable());
        //        migrators.add(new UserDatabaseTable());
        //        migrators.add(new PartnerFormClass());
        //        migrators.add(new PartnerTable());
        //        migrators.add(new AttributeGroupTable());
        //        migrators.add(new AttributeTable());
        migrators.add(new ActivityMigrator());
        //        migrators.add(new SiteTable());
        //        migrators.add(new ReportingPeriodTable());
    }

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        try(Connection connection = DriverManager.getConnection(
                "jdbc:mysql://" + args[0] + ":3306/activityinfo?zeroDateTimeBehavior=convertToNull", args[1], args[2])) {

            new MySqlMigrator().migrate(connection);
        }
    }

    public void migrate(Connection connection) throws Exception {

        final PreparedStatement statement = connection.prepareStatement(
                "REPLACE INTO resource (id, ownerId, json) VALUES (?, ?, ?)");

        for(final ResourceMigrator migrator : migrators) {

            migrator.getResources(connection, new ResourceWriter() {
                @Override
                public void write(Resource resource) {
                    if(resource == null) {
                        throw new NullPointerException(migrator.getClass().getName());
                    }
                    try {
                        statement.setString(1, resource.getId().asString());
                        statement.setString(2, resource.getId().asString());
                        statement.setString(3, Resources.toJson(resource));
                        statement.executeUpdate();
                    } catch(SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    public void migrate(Connection connection, ResourceWriter writer) throws Exception {
        for(ResourceMigrator migrator : migrators) {
           migrator.getResources(connection, writer);
        }
    }

}
