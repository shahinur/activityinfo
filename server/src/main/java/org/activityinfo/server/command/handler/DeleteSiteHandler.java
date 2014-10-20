package org.activityinfo.server.command.handler;

import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.DeleteSite;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.VoidResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.store.ResourceStore;

public class DeleteSiteHandler implements CommandHandler<DeleteSite> {

    private final ResourceStore store;

    @Inject
    public DeleteSiteHandler(ResourceStore store) {
        this.store = store;
    }

    @Override
    public CommandResult execute(DeleteSite cmd, User user) throws CommandException {
        store.delete(user.asAuthenticatedUser(), CuidAdapter.resourceId(CuidAdapter.SITE_DOMAIN, cmd.getSiteId()));
        return new VoidResult();
    }
}
