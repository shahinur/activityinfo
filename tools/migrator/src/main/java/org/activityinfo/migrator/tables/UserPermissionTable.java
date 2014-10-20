package org.activityinfo.migrator.tables;

import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.auth.UserPermission;
import org.activityinfo.model.auth.UserPermissionClass;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.activityinfo.model.legacy.CuidAdapter.DATABASE_DOMAIN;
import static org.activityinfo.model.legacy.CuidAdapter.databaseId;

public class UserPermissionTable extends ResourceMigrator {

    private MigrationContext context;

    public UserPermissionTable(MigrationContext context) {
        this.context = context;
    }

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {

        String sql = "SELECT P.* " +
                     "FROM userpermission P " +
                     "INNER JOIN userdatabase DB ON (P.DatabaseId=DB.DatabaseId) " +
                     "WHERE DB.dateDeleted is null and " + context.filter().databaseFilter("P.databaseId");

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {
                    int databaseId = rs.getInt("databaseId");

                    AuthenticatedUser user = new AuthenticatedUser(rs.getInt("userId"));
                    UserPermission rule = new UserPermission(
                            databaseId(databaseId), user.getUserResourceId());
                    rule.setOwner(false);
                    rule.setDesign(rs.getBoolean("AllowDesign"));
                    rule.setView(rs.getBoolean("AllowView"));
                    rule.setViewAll(rs.getBoolean("AllowViewAll"));
                    rule.setEdit(rs.getBoolean("AllowEdit"));
                    rule.setEditAll(rs.getBoolean("AllowEditAll"));
                    rule.setManageUsers(rs.getBoolean("AllowManageUsers"));
                    rule.setManageAllUsers(rs.getBoolean("AllowManageAllUsers"));
                    rule.setPartner(CuidAdapter.partnerInstanceId(databaseId, rs.getInt("partnerId")));

                    Resource resource = Resources.createResource();
                    resource.setId(rule.getId());
                    resource.setValue(UserPermissionClass.INSTANCE.toRecord(rule));
                    resource.setOwnerId(context.getIdStrategy().resourceId(DATABASE_DOMAIN, databaseId));
                    writer.writeResource(0, resource, null, null, 0);
                }
            }
        }
    }
}
