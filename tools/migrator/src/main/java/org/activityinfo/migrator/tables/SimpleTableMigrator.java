package org.activityinfo.migrator.tables;

import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.model.resource.Resource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class SimpleTableMigrator extends ResourceMigrator {

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {
        String sql = query();
        try(Statement stmt = connection.createStatement()) {
            try(ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()) {
                    writer.write(toResource(rs));
                }
            }
        }
    }

    protected void writeFormClass() {

    }

    protected String query() {
        return "select * from " + getTableName();
    }

    protected abstract Resource toResource(ResultSet rs) throws SQLException;

    public String getTableName() {
        return getClass()
                .getSimpleName()
                .replaceFirst("Table$", "")
                .toLowerCase();
    }
}
