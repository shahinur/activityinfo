package org.activityinfo.ui.client.page.entry.place;

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

import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.UserDatabaseDTO;
import org.activityinfo.ui.client.page.PageId;
import org.activityinfo.ui.client.page.app.Section;
import org.activityinfo.ui.client.page.common.grid.AbstractPagingGridPageState;

import java.util.Arrays;
import java.util.List;

public class DataEntryPlace extends AbstractPagingGridPageState {

    public static final PageId PAGE_ID = new PageId("data-entry");
    private Filter filter;

    public DataEntryPlace() {
        filter = new Filter();
    }

    public DataEntryPlace(Filter filter) {
        this.filter = filter;
    }

    public DataEntryPlace(ActivityDTO activity) {
        filter = new Filter();
        filter.addRestriction(DimensionType.Activity, activity.getId());
    }

    public DataEntryPlace(UserDatabaseDTO database) {
        filter = new Filter();
        filter.addRestriction(DimensionType.Database, database.getId());
    }

    @Deprecated
    public DataEntryPlace(int activityId) {
        filter = new Filter();
        filter.addRestriction(DimensionType.Activity, activityId);
    }

    public Filter getFilter() {
        return filter;
    }

    @Override
    public PageId getPageId() {
        return PAGE_ID;
    }

    @Override
    public String serializeAsHistoryToken() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PageId> getEnclosingFrames() {
        return Arrays.asList(PAGE_ID);
    }


    public DataEntryPlace setFilter(Filter filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public Section getSection() {
        return Section.DATA_ENTRY;
    }

}
