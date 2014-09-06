package org.activityinfo.migrator.tables;

import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class PartnerTable extends ResourceMigrator {

    private final MigrationContext context;

    public PartnerTable(MigrationContext context) {
        this.context = context;
    }

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {

        String sql = "SELECT P.*, PID.DatabaseId " +
                     "FROM partner P " +
                     "INNER JOIN partnerindatabase PID ON (P.PartnerId=PID.PartnerId) WHERE " +
                            context.filter().partnerFilter("P");


        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {
                    ResourceId id = context.getIdStrategy().partnerInstanceId(rs.getInt("DatabaseId"), rs.getInt("PartnerId"));
                    ResourceId classId = context.resourceId(PARTNER_FORM_CLASS_DOMAIN, rs.getInt("DatabaseId"));

                    FormInstance instance = new FormInstance(id, classId);
                    instance.set(field(classId, NAME_FIELD), rs.getString("name"));
                    instance.set(field(classId, FULL_NAME_FIELD), rs.getString("fullName"));

                    writer.writeResource(instance.asResource(), null, null);
                }
            }
        }
    }
}
