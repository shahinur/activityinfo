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

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.activityinfo.legacy.shared.command.GetSiteHistory;
import org.activityinfo.legacy.shared.command.GetSiteHistory.GetSiteHistoryResult;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.model.SiteHistoryDTO;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceVersion;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.StoreReader;

import javax.persistence.EntityManager;
import java.util.List;

public class GetSiteHistoryHandler implements CommandHandler<GetSiteHistory> {

    private ResourceStore store;
    private Provider<EntityManager> em;

    @Inject
    public GetSiteHistoryHandler(ResourceStore store, Provider<EntityManager> em) {
        this.store = store;
        this.em = em;
    }

    @Override
    public CommandResult execute(GetSiteHistory cmd, User user) throws CommandException {
        try (StoreReader storeReader = store.openReader(new AuthenticatedUser(user.getId()))) {
            boolean initial = true;
            List<SiteHistoryDTO> list = Lists.newArrayList();
            for (ResourceVersion version : storeReader.getSnapshots(cmd.getResourceId())) {
                User committer = em.get().find(User.class, version.getUserId());

                SiteHistoryDTO dto = new SiteHistoryDTO();
                dto.setInitial(initial);
                dto.setUserName(committer.getName());
                dto.setUserEmail(committer.getEmail());
                dto.setTimeCreated(version.getDateCommitted().getTime());
                list.add(dto);

                initial = false;
            }
            return new GetSiteHistoryResult(list);
        }
    }
}
