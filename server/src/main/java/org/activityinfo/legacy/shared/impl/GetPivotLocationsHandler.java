package org.activityinfo.legacy.shared.impl;
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

import com.google.api.client.util.Strings;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.GetPivotLocations;
import org.activityinfo.legacy.shared.command.PivotSites;
import org.activityinfo.legacy.shared.command.result.Bucket;
import org.activityinfo.legacy.shared.command.result.LocationResult;
import org.activityinfo.legacy.shared.model.LocationDTO;
import org.activityinfo.legacy.shared.reports.content.EntityCategory;
import org.activityinfo.legacy.shared.reports.model.Dimension;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuriyz on 11/03/2014.
 */
public class GetPivotLocationsHandler implements CommandHandlerAsync<GetPivotLocations, LocationResult> {

    public static final int LIMIT = 100;

    @Override
    public void execute(final GetPivotLocations command, ExecutionContext context, final AsyncCallback<LocationResult> callback) {
        context.execute(command.getPivotSites(), new AsyncCallback<PivotSites.PivotResult>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(PivotSites.PivotResult result) {
                callback.onSuccess(locationResult(command.getNameFilter(), result));
            }
        });
    }

    private LocationResult locationResult(String nameFilter, PivotSites.PivotResult result) {
        List<LocationDTO> locationList = new ArrayList<>();
        for (Bucket bucket : result.getBuckets()) {
            if (locationList.size() >= LIMIT) {
                break;
            }

            EntityCategory category = (EntityCategory) bucket.getCategory(new Dimension(DimensionType.Location));

            // apply name filter : if location label doesn't match nameFilter then skip
            if (!Strings.isNullOrEmpty(nameFilter) && !Strings.isNullOrEmpty(category.getLabel()) &&
                    !category.getLabel().contains(nameFilter)) {
                continue;
            }

            LocationDTO dto = new LocationDTO();
            dto.setId(category.getId());
            dto.setName(category.getLabel());
            locationList.add(dto);
        }
        return new LocationResult(locationList);
    }
}
