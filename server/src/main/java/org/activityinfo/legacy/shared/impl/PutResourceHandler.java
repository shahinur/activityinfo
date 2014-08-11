package org.activityinfo.legacy.shared.impl;

import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.legacy.shared.command.result.VoidResult;
import org.activityinfo.legacy.shared.model.PutResource;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;

public class PutResourceHandler implements CommandHandlerAsync<PutResource, VoidResult> {

    @Override
    public void execute(PutResource command, ExecutionContext context, final AsyncCallback<VoidResult> callback) {
        // TODO(alex) enforce permissions
        // TODO(alex) sqlite syntax

        Resource resource = Resources.fromJson(command.getJson());

        String sql = "REPLACE INTO resource (id, ownerId, content) VALUES (?, ?, ?)";
        Object[] parameters = new Object[] {
                resource.getId().asString(),
                resource.getOwnerId().asString(),
                command.getJson()
        };

        context.getTransaction().executeSql(sql, parameters, new SqlResultCallback() {
            @Override
            public void onSuccess(SqlTransaction tx, SqlResultSet results) {
                callback.onSuccess(null);
            }
        });
    }
}
