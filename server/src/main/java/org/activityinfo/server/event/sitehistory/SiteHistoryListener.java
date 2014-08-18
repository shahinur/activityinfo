package org.activityinfo.server.event.sitehistory;

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
import org.activityinfo.legacy.shared.command.CreateSite;
import org.activityinfo.legacy.shared.command.DeleteSite;
import org.activityinfo.legacy.shared.command.UpdateSite;
import org.activityinfo.server.event.CommandEvent;
import org.activityinfo.server.event.CommandEventListener;
import org.activityinfo.server.event.ServerEventBus;

public class SiteHistoryListener extends CommandEventListener {
    private final SiteHistoryProcessor siteHistoryProcessor;


    @Inject @SuppressWarnings("unchecked")
    public SiteHistoryListener(ServerEventBus serverEventBus, SiteHistoryProcessor siteHistoryProcessor) {
        super(serverEventBus, CreateSite.class, UpdateSite.class, DeleteSite.class);
        this.siteHistoryProcessor = siteHistoryProcessor;
    }

    @Override
    public void onEvent(CommandEvent event) {
        Integer userId = event.getUserId();
        Integer siteId = event.getSiteId();

        if (siteId != null && userId != null) {
            onEvent(event, userId, siteId);
        } else {
            LOGGER.warning("event fired without site and/or user!");
        }
    }

    private void onEvent(CommandEvent event, final int userId, final int siteId) {
        siteHistoryProcessor.process(event.getCommand(), userId, siteId);
    }
}
