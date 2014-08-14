package org.activityinfo.server.command.handler;

import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.VoidResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.impl.CommandHandlerAsync;
import org.activityinfo.legacy.shared.impl.ExecutionContext;
import org.activityinfo.legacy.shared.model.PutResource;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.store.cloudsql.MySqlResourceStore;

public class PutResourceHandler implements CommandHandler<PutResource> {

    private ResourceStore store;

    @Inject
    public PutResourceHandler(ResourceStore store) {
        this.store = store;
    }

    @Override
    public CommandResult execute(PutResource cmd, User user) throws CommandException {
        Resource resource = Resources.fromJson(cmd.getJson());
        UpdateResult updateResult = store.updateResource(CuidAdapter.userId(user.getId()), resource);

        return new VoidResult();
    }
}
