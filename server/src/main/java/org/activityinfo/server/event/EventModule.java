package org.activityinfo.server.event;

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

import org.activityinfo.server.event.sitechange.SiteChangeListener;
import org.activityinfo.server.event.sitechange.SiteChangeServlet;
import org.activityinfo.server.event.sitehistory.SiteHistoryListener;

import com.google.inject.servlet.ServletModule;

public class EventModule extends ServletModule {

    @Override
    protected void configureServlets() {
        // eventbus
        bind(ServerEventBus.class).asEagerSingleton();

        // listeners
        bind(SiteChangeListener.class).asEagerSingleton();
        bind(SiteHistoryListener.class).asEagerSingleton();

        // define endpoints for async callbacks
        serve(SiteChangeServlet.ENDPOINT).with(SiteChangeServlet.class);
    }
}