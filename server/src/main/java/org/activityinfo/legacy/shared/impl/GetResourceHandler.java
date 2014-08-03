package org.activityinfo.legacy.shared.impl;

import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.query.SqlQuery;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.legacy.shared.command.result.ResourceResult;
import org.activityinfo.legacy.shared.model.GetResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Fetches the given resource by id
 */
public class GetResourceHandler implements CommandHandlerAsync<GetResource, ResourceResult> {

    @Override
    public void execute(final GetResource command, ExecutionContext context, final AsyncCallback<ResourceResult> callback) {

        // TODO(alex) enforce permissions

        SqlQuery.select("id", "json").from("resource").where("id").in(command.getIds())
        .execute(context.getTransaction(), new SqlResultCallback() {
            @Override
            public void onSuccess(SqlTransaction tx, SqlResultSet results) {
                Set<String> expectedIds = Sets.newHashSet(command.getIds());
                ArrayList<String> encodedResults = Lists.newArrayList();
                for(SqlResultSetRow row : results.getRows()) {
                    String id = row.getString("id");
                    expectedIds.remove(id);
                    encodedResults.add(row.getString("json"));
                }
                if(!expectedIds.isEmpty()) {
                    callback.onFailure(new RuntimeException("Could not find resources: " + expectedIds));
                } else {
                    callback.onSuccess(new ResourceResult(encodedResults));
                }
            }
        });
    }
}
