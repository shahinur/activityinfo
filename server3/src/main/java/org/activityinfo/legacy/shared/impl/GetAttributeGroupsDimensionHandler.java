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

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.GetAttributeGroupsDimension;
import org.activityinfo.legacy.shared.command.PivotSites;
import org.activityinfo.legacy.shared.command.PivotSites.ValueType;
import org.activityinfo.legacy.shared.command.result.AttributeGroupResult;
import org.activityinfo.legacy.shared.command.result.Bucket;
import org.activityinfo.legacy.shared.model.AttributeGroupDTO;
import org.activityinfo.legacy.shared.reports.content.EntityCategory;
import org.activityinfo.legacy.shared.reports.model.Dimension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GetAttributeGroupsDimensionHandler implements CommandHandlerAsync<GetAttributeGroupsDimension,
        AttributeGroupResult> {

    @Override
    public void execute(GetAttributeGroupsDimension cmd,
                        final ExecutionContext context,
                        final AsyncCallback<AttributeGroupResult> callback) {

        // if the filter doesn't contain any activity, database or indicator values, just return an empty list
        if (!cmd.getFilter().isRestricted(DimensionType.Database) &&
            !cmd.getFilter().isRestricted(DimensionType.Activity) &&
            !cmd.getFilter().isRestricted(DimensionType.Indicator)) {

            callback.onSuccess(new AttributeGroupResult());
            return;
        }

        final Dimension dimension = new Dimension(DimensionType.AttributeGroup);

        final PivotSites query = new PivotSites();
        query.setFilter(cmd.getFilter());
        query.setDimensions(dimension);
        query.setValueType(ValueType.DIMENSION);

        context.execute(query, new AsyncCallback<PivotSites.PivotResult>() {
            @Override
            public void onSuccess(PivotSites.PivotResult result) {
                final List<AttributeGroupDTO> list = new ArrayList<AttributeGroupDTO>();

                // populate the resultlist
                for (Bucket bucket : result.getBuckets()) {
                    EntityCategory category = (EntityCategory) bucket.getCategory(dimension);
                    if (category != null) {
                        AttributeGroupDTO attributeGroup = new AttributeGroupDTO();
                        attributeGroup.setId(category.getId());
                        attributeGroup.setName(category.getLabel());
                        list.add(attributeGroup);
                    }
                }

                // sort the groups in the list by name (fallback on id)
                Collections.sort(list, new Comparator<AttributeGroupDTO>() {
                    @Override
                    public int compare(AttributeGroupDTO g1, AttributeGroupDTO g2) {
                        int result = g1.getName().compareToIgnoreCase(g2.getName());
                        if (result != 0) {
                            return result;
                        } else {
                            return ((Integer) g1.getId()).compareTo(g2.getId());
                        }
                    }
                });

                callback.onSuccess(new AttributeGroupResult(list));
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }
}
