package org.activityinfo.migrator.tables;

import com.google.api.client.util.Lists;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.model.resource.Resource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class SimpleTableMigrator extends ResourceMigrator {


    @Override
    public final List<Resource> getResources(Connection connection) throws SQLException {
        List<Resource> resources = Lists.newArrayList();
        String sql = query();
        try(Statement stmt = connection.createStatement()) {
            try(ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()) {
                    resources.add(toResource(rs));
                }
            }
        }
        return resources;
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
