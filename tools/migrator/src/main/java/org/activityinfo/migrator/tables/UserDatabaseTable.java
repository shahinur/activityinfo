package org.activityinfo.migrator.tables;

import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.ReferenceValue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class UserDatabaseTable extends ResourceMigrator {


    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {
        String sql = "select * from userdatabase where datedeleted is null";
        try(Statement stmt = connection.createStatement()) {
            try(ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()) {
                    writeDatabase(writer, rs);
                }
            }
        }
    }

    private void writeDatabase(ResourceWriter writer, ResultSet rs) throws Exception {

        ResourceId id = resourceId(DATABASE_DOMAIN, rs.getInt("DatabaseId"));

        FormInstance database = new FormInstance(id, FolderClass.CLASS_ID)
        .setOwnerId(Resources.ROOT_ID)
        .set(FolderClass.LABEL_FIELD_ID, rs.getString("name"))
        .set(FolderClass.DESCRIPTION_FIELD_ID, rs.getString("fullName"))
        .set(CuidAdapter.field(FormClass.CLASS_ID, LOCATION_FIELD),
                new ReferenceValue(resourceId(COUNTRY_DOMAIN, rs.getInt("CountryId"))));

        writer.writeResource(database.asResource());


        ResourceId ownerId = resourceId(USER_DOMAIN, rs.getInt("OwnerUserId"));
        AccessControlRule rule = new AccessControlRule(database.getId(), ownerId);
        rule.setOwner(true);

        writer.writeResource(rule.asResource());

    }
}
