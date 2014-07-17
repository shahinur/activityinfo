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
import org.activityinfo.legacy.shared.command.RemoveProject;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.VoidResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.entity.change.ChangeHandler;
import org.activityinfo.server.entity.change.ChangeRequestBuilder;

public class RemoveProjectHandler implements CommandHandler<RemoveProject> {

    private ChangeHandler changeHandler;

    @Inject
    public RemoveProjectHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    @Override
    public CommandResult execute(RemoveProject cmd, User user) throws CommandException {


        changeHandler.execute(ChangeRequestBuilder.delete()
                                                  .setEntityType("Project")
                                                  .setEntityId(cmd.getId())
                                                  .setUser(user));


        return new VoidResult();
    }

}
