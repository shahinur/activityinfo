package org.activityinfo.migrator;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

class MySqlResourceWriter implements ResourceWriter {

    private final Connection connection;
    private int count;
    private PreparedStatement statement;

    public MySqlResourceWriter(Connection connection) throws SQLException {
        this.connection = connection;

        connection.setAutoCommit(false);

        deleteAllResources(connection);

        this.statement = connection.prepareStatement(
                "insert INTO resource (id, ownerId, classId, json) VALUES (?, ?, ?, ?)");

        count = 0;
    }

    private void deleteAllResources(Connection connection) throws SQLException {
        System.out.println("Dropping and recreating resource table...");
        Statement statement = connection.createStatement();
        statement.executeUpdate("drop table resource");
        statement.executeUpdate("create table resource (id varchar(64) binary primary key, " +
                                "ownerId varchar(64) binary not null," +
                                "classId varchar(64) binary," +
                                "json longtext)");
        statement.close();
        connection.commit();
        System.out.println("Resource table truncated.");
    }


    @Override
    public void write(Resource resource) {
        if(resource == null) {
            throw new NullPointerException("resource");
        }
        try {

            statement.setString(1, resource.getId().asString());
            statement.setString(2, resource.getOwnerId().asString());
            statement.setString(3, resource.isString("classId"));
            statement.setString(4, Resources.toJson(resource));
            statement.addBatch();
            count ++ ;

            if(count % 10000 == 0) {
                statement.executeBatch();
                connection.commit();

                System.out.println("..." + count + " resources committed...");
            }
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws SQLException {
        System.out.println("Closing... " + count + " resources written in total.");
        statement.executeBatch();
        statement.close();
        connection.commit();
        System.out.println(count + " resources written.");
    }
}
