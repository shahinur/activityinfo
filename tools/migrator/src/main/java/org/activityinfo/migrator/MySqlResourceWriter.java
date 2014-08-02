package org.activityinfo.migrator;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class MySqlResourceWriter implements ResourceWriter {

    private final Connection connection;
    private int count;
    private final PreparedStatement statement;

    public MySqlResourceWriter(Connection connection) throws SQLException {
        this.connection = connection;
        this.statement = this.connection.prepareStatement("insert INTO resource (id, ownerId, json) VALUES (?, ?, ?)");
        connection.setAutoCommit(false);
        count = 0;
    }


    @Override
    public void write(Resource resource) {
        if(resource == null) {
            throw new NullPointerException("resource");
        }
        try {
            //System.out.println(resource.getId());
            statement.setString(1, resource.getId().asString());
            statement.setString(2, resource.getId().asString());
            statement.setString(3, Resources.toJson(resource));
            statement.addBatch();
            count ++ ;

            if(count % 1000 == 0) {
                statement.executeBatch();
                connection.commit();
                System.out.println(count + " resources written...");
            }
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws SQLException {
        statement.executeBatch();
        connection.commit();
        System.out.println(count + " resources written.");
        statement.close();
    }
}
