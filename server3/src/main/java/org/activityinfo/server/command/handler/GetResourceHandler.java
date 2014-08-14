package org.activityinfo.server.command.handler;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.ResourceResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.model.GetResource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.store.ResourceStore;

import java.util.ArrayList;

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
            encodedResults.add(Resources.toJson(store.get(ResourceId.valueOf(id))));
        }
        return new ResourceResult(encodedResults);
    }
}
