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
import org.activityinfo.legacy.shared.command.AddPartner;
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

/**
 * @author Alex Bertram
 * @see org.activityinfo.legacy.shared.command.AddPartner
 */
public class AddPartnerHandler implements CommandHandler<AddPartner> {

    private final ResourceStore store;
    private final KeyGenerator keyGenerator = new KeyGenerator();

    @Inject
    public AddPartnerHandler(ResourceStore store) {
        this.store = store;
    }


    @Override @SuppressWarnings("unchecked")
    public CommandResult execute(AddPartner cmd, User user) throws CommandException {


        int partnerId = keyGenerator.generateInt();

        ResourceId id = CuidAdapter.resourceId(PARTNER_DOMAIN, partnerId);
        ResourceId classId = CuidAdapter.partnerFormClass(cmd.getDatabaseId());

        FormInstance instance = new FormInstance(id, classId);
        instance.set(field(classId, NAME_FIELD), cmd.getPartner().getName());
        instance.set(field(classId, FULL_NAME_FIELD), cmd.getPartner().getFullName());

        store.create(user.asAuthenticatedUser(), instance.asResource());

        return new CreateResult(partnerId);
    }
}
