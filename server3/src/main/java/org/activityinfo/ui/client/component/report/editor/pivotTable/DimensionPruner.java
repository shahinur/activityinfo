package org.activityinfo.ui.client.component.report.editor.pivotTable;

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

import com.google.common.collect.Sets;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.IndicatorDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.legacy.shared.model.UserDatabaseDTO;
import org.activityinfo.legacy.shared.reports.model.AttributeGroupDimension;
import org.activityinfo.legacy.shared.reports.model.Dimension;
import org.activityinfo.legacy.shared.reports.model.PivotTableReportElement;
import org.activityinfo.ui.client.EventBus;
import org.activityinfo.ui.client.page.report.HasReportElement;
import org.activityinfo.ui.client.page.report.ReportChangeHandler;
import org.activityinfo.ui.client.page.report.ReportEventBus;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Removes inapplicable dimensions from the model after a user change.
 * <p/>
 * <p/>
 * For example, if an attribute dimension related to activity X is selected, but
 * all indicators from Activity are removed, then we need to remove the
 * dimension.
 */
public class DimensionPruner implements HasReportElement<PivotTableReportElement> {

    private static final Logger LOGGER = Logger.getLogger(DimensionPruner.class.getName());

    private final ReportEventBus reportEventBus;
    private PivotTableReportElement model;
    private Dispatcher dispatcher;

    @Inject
    public DimensionPruner(EventBus eventBus, Dispatcher dispatcher) {
        super();
        this.dispatcher = dispatcher;
        this.reportEventBus = new ReportEventBus(eventBus, this);
        this.reportEventBus.listen(new ReportChangeHandler() {

            @Override
            public void onChanged() {
                DimensionPruner.this.onChanged();
            }
        });
    }

    protected void onChanged() {
        dispatcher.execute(new GetSchema(), new AsyncCallback<SchemaDTO>() {

            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(SchemaDTO result) {
                pruneModel(result);
            }
        });
    }

    private void pruneModel(SchemaDTO schema) {
        Set<ActivityDTO> activityIds = getSelectedActivities(schema);
        Set<AttributeGroupDimension> dimensions = getSelectedAttributes(schema);
        boolean dirty = false;
        for (AttributeGroupDimension dim : dimensions) {
            if (!isApplicable(schema, activityIds, dim)) {
                LOGGER.fine("Removing attribute group " + dim.getAttributeGroupId());
                model.getRowDimensions().remove(dim);
                model.getColumnDimensions().remove(dim);
                dirty = true;
            }
        }
        if (dirty) {
            reportEventBus.fireChange();
        }
    }

    private boolean isApplicable(SchemaDTO schema, Set<ActivityDTO> activities, AttributeGroupDimension dim) {

        String attributeName = schema.getAttributeGroupNameSafe(dim.getAttributeGroupId());

        for (ActivityDTO activity : activities) {
            if (activity.getAttributeGroupByName(attributeName) != null) {
                return true;
            }
        }
        return false;
    }

    private Set<AttributeGroupDimension> getSelectedAttributes(SchemaDTO schema) {
        Set<AttributeGroupDimension> dimensions = Sets.newHashSet();
        for (Dimension dim : model.allDimensions()) {
            if (dim instanceof AttributeGroupDimension) {
                dimensions.add((AttributeGroupDimension) dim);
            }
        }
        return dimensions;
    }

    private Set<ActivityDTO> getSelectedActivities(SchemaDTO schema) {
        Set<ActivityDTO> activities = Sets.newHashSet();
        Set<Integer> indicatorIds = Sets.newHashSet(model.getFilter().getRestrictions(DimensionType.Indicator));
        for (UserDatabaseDTO db : schema.getDatabases()) {
            for (ActivityDTO activity : db.getActivities()) {
                for (IndicatorDTO indicator : activity.getIndicators()) {
                    if (indicatorIds.contains(indicator.getId())) {
                        activities.add(activity);
                    }
                }
            }
        }
        return activities;
    }

    @Override
    public void bind(PivotTableReportElement model) {
        this.model = model;
    }

    @Override
    public PivotTableReportElement getModel() {
        return model;
    }

    @Override
    public void disconnect() {
        reportEventBus.disconnect();
    }
}
