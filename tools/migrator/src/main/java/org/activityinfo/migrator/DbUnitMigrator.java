package org.activityinfo.migrator;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.LowerCaseDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mssql.InsertIdentityOperation;
import org.dbunit.ext.mysql.MySqlConnection;

import java.io.*;
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

//        if(!file.getName().contains("brac")) {
//            return;
//        }
        System.out.println("Migrating " + file.getName());

        loadDataset(connection, file);
        dumpToJson(connection, file);
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


    private void dumpToJson(Connection connection, File dbUnitFile) throws IOException, SQLException {
        File jsonFile = new File(dbUnitFile.getParentFile(),
                dbUnitFile.getName().replaceFirst("\\.db\\.xml$", ".json"));
        new MySqlMigrator().migrate(connection, jsonFile);
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
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/activityinfo-test", "root", "root");
    }
}