package org.activityinfo.server.command.handler;

import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.query.SqlQuery;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.ResourceResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.impl.CommandHandlerAsync;
import org.activityinfo.legacy.shared.impl.ExecutionContext;
import org.activityinfo.legacy.shared.model.GetResource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.store.ResourceStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Fetches the given resource by id
 */
public class GetResourceHandler implements CommandHandler<GetResource> {


    private ResourceStore store;

    @Inject
    public GetResourceHandler(ResourceStore store) {
        this.store = store;
    }

    @Override
    public CommandResult execute(GetResource command, User user) throws CommandException {
        ArrayList<String> encodedResults = Lists.newArrayList();
        for(String id : command.getIds()) {
            encodedResults.add(Resources.toJson(store.get(ResourceId.create(id))));
        }
        return new ResourceResult(encodedResults);
    }
}
