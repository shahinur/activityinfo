package org.activityinfo.ui.client.page.entry.form;

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

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.client.KeyGenerator;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.command.GetFormViewModel;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.LocationDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.legacy.shared.model.LockedPeriodSet;
import org.activityinfo.ui.client.page.entry.location.LocationDialog;

public class SiteDialogLauncher {

    private final Dispatcher dispatcher;

    public SiteDialogLauncher(Dispatcher dispatcher) {
        super();
        this.dispatcher = dispatcher;
    }

    public void addSite(final Filter filter, final SiteDialogCallback callback) {
        if (filter.isDimensionRestrictedToSingleCategory(DimensionType.Activity)) {
            int activityId = filter.getRestrictedCategory(DimensionType.Activity);

            dispatcher.execute(new GetFormViewModel(activityId), new AsyncCallback<ActivityDTO>() {

                @Override
                public void onFailure(Throwable caught) {
                    Log.error("Unable to add site", caught);
                }

                @Override
                public void onSuccess(ActivityDTO activity) {
                    Log.trace("adding site for activity " + activity + ", locationType = " + activity.getLocationType());

                    if (activity.getLocationType().isAdminLevel()) {
                        addNewSiteWithBoundLocation(activity, callback);

                    } else if (activity.getLocationType().isNationwide()) {
                        addNewSiteWithNoLocation(activity, callback);

                    } else {
                        chooseLocationThenAddSite(activity, callback);
                    }
                }
            });
        }
    }

    public void editSite(final SiteDTO site, final SiteDialogCallback callback) {
        dispatcher.execute(new GetSchema(), new AsyncCallback<SchemaDTO>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(SchemaDTO schema) {
                ActivityDTO activity = schema.getActivityById(site.getActivityId());

                // check whether the site has been locked
                // (this only applies to Once-reported activities because
                //  otherwise the date criteria applies to the monthly report)
                if (activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {
                    LockedPeriodSet locks = new LockedPeriodSet(schema);
                    if (locks.isLocked(site)) {
                        MessageBox.alert(I18N.CONSTANTS.lockedSiteTitle(), I18N.CONSTANTS.siteIsLocked(), null);
                        return;
                    }
                }

                SiteDialog dialog = new SiteDialog(dispatcher, activity);
                dialog.showExisting(site, callback);
            }
        });
    }

    private void chooseLocationThenAddSite(final ActivityDTO activity, final SiteDialogCallback callback) {
        LocationDialog dialog = new LocationDialog(dispatcher,
                activity.getLocationType());

        dialog.show(new LocationDialog.Callback() {

            @Override
            public void onSelected(LocationDTO location, boolean isNew) {
                SiteDTO newSite = new SiteDTO();
                newSite.setActivityId(activity.getId());
                newSite.setLocation(location);

                SiteDialog dialog = new SiteDialog(dispatcher, activity);
                dialog.showNew(newSite, location, isNew, callback);
            }
        });
    }

    private void addNewSiteWithBoundLocation(ActivityDTO activity, SiteDialogCallback callback) {
        SiteDTO newSite = new SiteDTO();
        newSite.setActivityId(activity.getId());

        LocationDTO location = new LocationDTO();
        location.setId(new KeyGenerator().generateInt());
        location.setLocationTypeId(activity.getLocationTypeId());

        SiteDialog dialog = new SiteDialog(dispatcher, activity);
        dialog.showNew(newSite, location, true, callback);
    }

    private void addNewSiteWithNoLocation(ActivityDTO activity, SiteDialogCallback callback) {
        SiteDTO newSite = new SiteDTO();
        newSite.setActivityId(activity.getId());

        LocationDTO location = new LocationDTO();
        location.setId(activity.getLocationTypeId());
        location.setLocationTypeId(activity.getLocationTypeId());

        SiteDialog dialog = new SiteDialog(dispatcher, activity);
        dialog.showNew(newSite, location, true, callback);
    }
}
