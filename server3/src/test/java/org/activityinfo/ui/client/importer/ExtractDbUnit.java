package org.activityinfo.ui.client.importer;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;


@SuppressWarnings({"NonJREEmulationClassesInClientCode", "AppEngineForbiddenCode"})
public class ExtractDbUnit {

    /**
     * Utility to create a dbunit xml file from a local mysql database
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/activityinfo?zeroDateTimeBehavior=convertToNull", "root", "root");
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        // partial database export
        QueryDataSet partialDataSet = new QueryDataSet(connection);
//
//        partialDataSet.addTable("userlogin", "select * from userlogin where userid in " +
//                "(select owneruserid from userdatabase where databaseid=1100)");

        partialDataSet.addTable("country", "select * from country where countryid=451");
        partialDataSet.addTable("adminlevel", "select * from adminlevel where countryid=451");
        partialDataSet.addTable("adminentity", "select AdminEntityId, AdminEntityParentId, AdminLevelId, Name" +
                " from adminentity where adminlevelid in " +
                "(select adminlevelid from adminlevel where CountryId=451)");
        partialDataSet.addTable("locationtype", "select * from locationtype" +
                " where countryid = 451 and BoundAdminLevelId is null");
//        partialDataSet.addTable("location", "select * from location where locationtypeid in " +
//                "(select locationtypeid from locationtype where countryid = 451 and BoundAdminLevelId is null)");
//        partialDataSet.addTable("locationadminlink", "select * from locationadminlink where locationid in " +
//                "(select locationid from location where LocationTypeID in " +
//                "(select locationtypeid from locationtype where countryid = 451 and BoundAdminLevelId is null))");


        FlatXmlDataSet.write(partialDataSet, new FileOutputStream("src/test/resources/dbunit/somalia-admin.db.xml"));

    }
}
