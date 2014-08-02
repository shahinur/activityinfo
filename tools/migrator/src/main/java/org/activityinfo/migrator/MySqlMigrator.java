package org.activityinfo.migrator;


import com.google.common.collect.Lists;
import org.activityinfo.migrator.tables.ActivityTable;

import java.sql.Connection;
import java.sql.DriverManager;
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
        migrators.add(new ActivityTable());
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

        MySqlResourceWriter writer = new MySqlResourceWriter(connection);


        for(final ResourceMigrator migrator : migrators) {
            migrator.getResources(connection, writer);
        }

        writer.close();
    }

    public void migrate(Connection connection, ResourceWriter writer) throws Exception {
        for(ResourceMigrator migrator : migrators) {
           migrator.getResources(connection, writer);
        }
    }

}
