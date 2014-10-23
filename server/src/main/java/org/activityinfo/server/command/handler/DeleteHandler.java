package org.activityinfo.server.command.handler;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.Delete;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.store.ResourceStore;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class DeleteHandler implements CommandHandler<Delete> {

    private final ResourceStore store;

    @Inject
    public DeleteHandler(ResourceStore store) {
        this.store = store;
    }

    @Override
    public CommandResult execute(Delete cmd, User user) {
        switch(cmd.getEntityName()) {
            case "Partner":
                deleteResource(user, cmd, PARTNER_DOMAIN);
                break;
            case "Project":
                deleteResource(user, cmd, PROJECT_DOMAIN);
                break;
            case "Activity":
                deleteResource(user, cmd, ACTIVITY_DOMAIN);
                break;
            case "Site":
                deleteResource(user, cmd, SITE_DOMAIN);
                break;
            case "UserDatabase":
                deleteResource(user, cmd, DATABASE_DOMAIN);
                break;

            default:
                throw new IllegalArgumentException("entity type: " + cmd.getEntityName());
        }
        return null;
    }

    private void deleteResource(User user, Delete cmd, char domain) {
        store.delete(user.asAuthenticatedUser(), CuidAdapter.resourceId(domain, cmd.getId()));
    }
}
