package org.activityinfo.migrator.tables;

import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class PartnerTable extends ResourceMigrator {

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {

        String sql = "SELECT P.*, PID.DatabaseId " +
                     "FROM partner P " +
                     "INNER JOIN partnerindatabase PID ON (P.PartnerId=PID.PartnerId)";


        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {
                    ResourceId id = partnerInstanceId(rs.getInt("DatabaseId"), rs.getInt("PartnerId"));
                    ResourceId classId = partnerFormClass(rs.getInt("DatabaseId"));

                    FormInstance instance = new FormInstance(id, classId);
                    instance.set(field(classId, NAME_FIELD), rs.getString("name"));
                    instance.set(field(classId, FULL_NAME_FIELD), rs.getString("fullName"));

                    writer.write(instance.asResource());
                }
            }
        }
    }
}
