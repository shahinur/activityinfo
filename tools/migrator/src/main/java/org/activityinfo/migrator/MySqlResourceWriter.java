package org.activityinfo.migrator;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class MySqlResourceWriter implements ResourceWriter {

    private long version = 1;

    private final Connection connection;
    private int count;
    private PreparedStatement statement;

    public MySqlResourceWriter(Connection connection) throws SQLException {
        this.connection = connection;

        connection.setAutoCommit(false);

        this.statement = connection.prepareStatement(
                "insert INTO resource (id, version, sub_tree_version, owner_id, class_id, label, content) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)");

        count = 0;
    }


    @Override
    public void write(Resource resource) {
        if(resource == null) {
            throw new NullPointerException("resource");
        }
        try {

            statement.setString(1, resource.getId().asString());
            statement.setLong(2, version);
            statement.setLong(3, version);
            statement.setString(4, resource.getOwnerId().asString());
            statement.setString(5, resource.isString("classId"));
            statement.setString(6, getLabel(resource));
            statement.setString(7, Resources.toJson(resource));
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

    private String getLabel(Resource resource) {
        String classId = resource.isString("classId");
        if(FormClass.CLASS_ID.asString().equals(classId)) {
            return resource.getString(FormClass.LABEL_FIELD_ID);
        } else if(FolderClass.CLASS_ID.asString().equals(classId)) {
            return resource.isString(FolderClass.LABEL_FIELD_ID.asString());
        }
        return null;
    }

    public void close() throws SQLException {
        System.out.println("Closing... " + count + " resources written in total.");
        statement.executeBatch();
        statement.close();
        connection.commit();
        System.out.println(count + " resources written.");
    }
}
