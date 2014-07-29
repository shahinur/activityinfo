package org.activityinfo.legacy.shared.impl;

import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.query.SqlQuery;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.ResourceResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.model.GetResource;
import org.activityinfo.server.command.handler.CommandHandler;
import org.activityinfo.server.database.hibernate.entity.User;

/**
 * Fetches the given resource by id
 */
public class GetResourceHandler implements CommandHandlerAsync<GetResource, ResourceResult> {

    @Override
    public void execute(GetResource command, ExecutionContext context, final AsyncCallback<ResourceResult> callback) {
        SqlQuery.select("json").from("resource").where("id").equalTo(command.getId())
        .execute(context.getTransaction(), new SqlResultCallback() {
            @Override
            public void onSuccess(SqlTransaction tx, SqlResultSet results) {
                ResourceResult result = new ResourceResult();
                result.setJson(results.getRow(0).getString("json"));
                callback.onSuccess(result);
            }
        });
    }
}
