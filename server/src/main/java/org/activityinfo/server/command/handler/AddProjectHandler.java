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
import org.activityinfo.legacy.shared.command.AddProject;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.CreateResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.legacy.KeyGenerator;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.store.ResourceStore;

import static org.activityinfo.model.legacy.CuidAdapter.*;

/*
 * Adds given Project to the database
 */
public class AddProjectHandler implements CommandHandler<AddProject> {

    private final ResourceStore store;

    @Inject
    public AddProjectHandler(ResourceStore store) {
        this.store = store;
    }

    @Override
    public CommandResult execute(AddProject cmd, User user) throws CommandException {

        int projectId = KeyGenerator.get().generateInt();

        ResourceId id = CuidAdapter.resourceId(PROJECT_DOMAIN, projectId);
        ResourceId classId = CuidAdapter.projectFormClass(cmd.getDatabaseId());

        FormInstance instance = new FormInstance(id, classId);
        instance.set(field(classId, NAME_FIELD), cmd.getProjectDTO().getName());
        instance.set(field(classId, FULL_NAME_FIELD), cmd.getProjectDTO().getName());

        store.create(user.asAuthenticatedUser(), instance.asResource());

        return new CreateResult(projectId);
    }
}