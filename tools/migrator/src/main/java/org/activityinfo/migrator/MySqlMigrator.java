package org.activityinfo.migrator;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.collect.Lists;
import org.activityinfo.migrator.globals.GeographicDatabase;
import org.activityinfo.migrator.tables.*;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.serialization.jackson.ResourceWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Migrates data from the legacy MySQL table structure
 */
public class MySqlMigrator {

    private JsonGenerator json;

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        new MySqlMigrator().migrate();
    }

    private List<ResourceMigrator> migrators = Lists.newArrayList();

    public MySqlMigrator() {
        migrators.add(new GeographicDatabase());
        migrators.add(new CountryTable());
        migrators.add(new AdminLevelTable());
        migrators.add(new AdminEntityTable());
        migrators.add(new LocationTypeTable());
        migrators.add(new LocationTable());
        migrators.add(new UserLoginTable());
        migrators.add(new UserDatabaseTable());
        migrators.add(new PartnerFormClass());
        migrators.add(new PartnerTable());
        migrators.add(new AttributeGroupTable());
        migrators.add(new AttributeTable());
        migrators.add(new ActivityTable());
        migrators.add(new SiteTable());
        migrators.add(new ReportingPeriodTable());
    }

    public void migrate() throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.mysql.jdbc.Driver");
        try(Connection connection = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/activityinfo", "root", "root")) {

            migrate(connection, new File("resources.json"));
        }
    }

    public void migrate(Connection connection, File jsonFile) throws SQLException, IOException {
        JsonFactory jsonFactory = new JsonFactory();
        json = jsonFactory.createGenerator(new FileWriter(jsonFile));
        json.useDefaultPrettyPrinter();
        json.writeStartArray();

        for(ResourceMigrator migrator : migrators) {
            for(Resource resource : migrator.getResources(connection)) {
                if(resource == null) {
                    throw new NullPointerException(migrator.getClass().getName());
                }
                try {
                    ResourceWriter.writeResource(json, resource);
                } catch(Exception e) {
                    throw new RuntimeException("Exception while serializing " + migrator.getClass().getName() +
                        ": " + resource, e);
                }
            }
        }

        json.writeEndArray();
        json.close();
    }


}
