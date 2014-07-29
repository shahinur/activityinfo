package org.activityinfo.migrator.tables;

import com.google.api.client.util.Lists;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.model.resource.Resource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.activityinfo.model.shared.CuidAdapter.*;

public class PartnerTable extends ResourceMigrator {

    @Override
    public Iterable<Resource> getResources(Connection connection) throws SQLException {

        String sql = "SELECT P.*, PID.DatabaseId " +
                     "FROM partner P " +
                     "INNER JOIN partnerindatabase PID ON (P.PartnerId=PID.PartnerId)";

        List<Resource> resources = Lists.newArrayList();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {
                    Resource resource = Resources.createResource()
                    .setId(partnerInstanceId(rs.getInt("DatabaseId"), rs.getInt("PartnerId")))
                    .setOwnerId(partnerFormClass(rs.getInt("DatabaseId")))
                    .set(CLASS_FIELD, partnerFormClass(rs.getInt("DatabaseId")))
                    .set(NAME_FIELD, rs.getString("name"))
                    .set(FULL_NAME_FIELD, rs.getString("fullName"));

                    resources.add(resource);
                }
            }
        }
        return resources;
    }
}
