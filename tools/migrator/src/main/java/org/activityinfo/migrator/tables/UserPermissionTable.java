package org.activityinfo.migrator.tables;

import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.activityinfo.model.legacy.CuidAdapter.*;
import static org.activityinfo.model.legacy.CuidAdapter.FULL_NAME_FIELD;
import static org.activityinfo.model.legacy.CuidAdapter.field;

public class UserPermissionTable extends ResourceMigrator {

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {

        String sql = "SELECT P.*, PID.DatabaseId " +
                     "FROM userpermission P " +
                     "INNER JOIN userdatabase DB ON (P.DatabaseId=DB.DatabaseId)";

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {
                    ResourceId id = partnerInstanceId(rs.getInt("DatabaseId"), rs.getInt("PartnerId"));
                    ResourceId classId = partnerFormClass(rs.getInt("DatabaseId"));

                    FormInstance instance = new FormInstance(id, classId);
                    instance.set(field(classId, NAME_FIELD), rs.getString("name"));
                    instance.set(field(classId, FULL_NAME_FIELD), rs.getString("fullName"));

                    writer.writeResource(instance.asResource());
                }
            }
        }
    }
}
