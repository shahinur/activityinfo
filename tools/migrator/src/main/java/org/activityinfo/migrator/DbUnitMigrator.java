package org.activityinfo.migrator;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.LowerCaseDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mssql.InsertIdentityOperation;
import org.dbunit.ext.mysql.MySqlConnection;

import java.io.File;
import java.io.FileReader;
import java.sql.*;

/**
 * Migrates DbUnit test cases to json resource dump files
 */
public class DbUnitMigrator {

    private static final String LIQUIBASE_TABLE_PREFIX = "databasechangelog";


    public static void main(String[] args) throws Exception {
        new DbUnitMigrator().migrate();
    }

    public DbUnitMigrator() {

    }

    public void migrate() throws Exception {
        // find all dbunit fixtures recursively
        try(Connection connection = openConnection()) {
            File root = new File(".");
            findDbUnitFixtures(connection, root);
        }
    }

    private void findDbUnitFixtures(Connection connection, File dir) throws Exception {
        File[] children = dir.listFiles();
        if(children != null) {
            for(File file : children) {
                if(file.getName().endsWith(".db.xml")) {
                    migrateDbUnit(connection, file);
                } else if(file.isDirectory()) {
                    findDbUnitFixtures(connection, file);
                }
            }
        }
    }

    private void migrateDbUnit(Connection connection, File file) throws Exception {

        System.out.println("Migrating " + file.getName());

        try {
            loadDataset(connection, file);
            migrate(connection, file);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void migrate(Connection jdbcConnection, File file) throws Exception {

        File projectDir = new File(file.getParent() + "../../../../../../").getCanonicalFile();
        File resourcesDir = new File(projectDir + "/store/testing/src/main/resources").getCanonicalFile();

        if(!resourcesDir.exists()) {
            throw new IllegalStateException("No such dir: " + resourcesDir.getAbsolutePath());
        }

        String fileName = file.getName().replace(".db.xml", ".json");
        File migratedFile = new File(resourcesDir, fileName);

        JsonTestUnitWriter writer = new JsonTestUnitWriter(migratedFile);
        new MySqlMigrator().migrate(jdbcConnection, writer);
        writer.finish();
    }

    private void loadDataset(Connection connection, File file) throws Exception {
        removeAllRows(connection);


        IDataSet dataSet;
        try(FileReader reader = new FileReader(file)) {
            dataSet = new LowerCaseDataSet(
                    new FlatXmlDataSetBuilder()
                            .setDtdMetadata(true)
                            .setColumnSensing(true)
                            .build(reader));
        }

        IDatabaseConnection dbUnitConnection = new MySqlConnection(connection, null);
        InsertIdentityOperation.INSERT.execute(dbUnitConnection, dataSet);
    }

    private void removeAllRows(Connection connection) throws Exception {
        connection.setAutoCommit(false);
        try(Statement statement = connection.createStatement()) {

            statement.execute("SET foreign_key_checks = 0");

            try(ResultSet tables = connection.getMetaData().getTables(null, null, null, new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString(3);
                    if (!tableName.toLowerCase().startsWith(LIQUIBASE_TABLE_PREFIX)) {
                        statement.execute("DELETE FROM " + tableName);
                    }
                }
            }
            statement.execute("SET foreign_key_checks = 1");
            statement.close();
            connection.commit();

        } finally {
            connection.setAutoCommit(true);
        }
    }

    private Connection openConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/activityinfo-test?zeroDateTimeBehavior=convertToNull",
                "root", "root");
    }
}