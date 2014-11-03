package org.activityinfo.legacy.shared.command;
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

import org.activityinfo.legacy.shared.command.result.LocationResult;

/**
 * @author yuriyz on 11/03/2014.
 */
public class GetPivotLocations implements Command<LocationResult> {

    private PivotSites pivotSites;
    private String nameFilter;

    public GetPivotLocations() {
    }

    public GetPivotLocations(PivotSites pivotSites) {
        this.pivotSites = pivotSites;
    }

    public GetPivotLocations(PivotSites pivotSites, String nameFilter) {
        this.pivotSites = pivotSites;
        this.nameFilter = nameFilter;
    }

    public PivotSites getPivotSites() {
        return pivotSites;
    }

    public void setPivotSites(PivotSites pivotSites) {
        this.pivotSites = pivotSites;
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }
}
