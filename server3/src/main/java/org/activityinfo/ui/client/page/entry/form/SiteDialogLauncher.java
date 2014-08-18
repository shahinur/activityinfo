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

import com.google.gwt.user.client.Window;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.client.component.form.FormDialog;
import org.activityinfo.ui.client.component.form.FormDialogCallback;

public class SiteDialogLauncher {

    private final Dispatcher dispatcher;
    private ResourceLocator resourceLocator;

    public SiteDialogLauncher(Dispatcher dispatcher, ResourceLocator resourceLocator) {
        super();
        this.dispatcher = dispatcher;
        this.resourceLocator = resourceLocator;
    }

    public void addSite(final Filter filter, final SiteDialogCallback callback) {
        if (filter.isDimensionRestrictedToSingleCategory(DimensionType.Activity)) {
            int activityId = filter.getRestrictedCategory(DimensionType.Activity);

            ResourceId formClassId = CuidAdapter.activityFormClass(activityId);
            ResourceId instanceId = CuidAdapter.newLegacyFormInstanceId(formClassId);
            FormInstance newInstance = new FormInstance(instanceId, formClassId);

            FormDialog formDialog = new FormDialog(resourceLocator);
            formDialog.show(newInstance, new FormDialogCallback() {
                @Override
                public void onPersisted(FormInstance instance) {
                    Window.alert("Refresh the grid to see changes.");
                }
            });
        }
    }

    public void editSite(final SiteDTO site, final SiteDialogCallback callback) {

        FormDialog formDialog = new FormDialog(resourceLocator);
        formDialog.show(site.getInstanceId(), new FormDialogCallback() {
            @Override
            public void onPersisted(FormInstance instance) {
                Window.alert("Refresh the grid to see changes.");
            }
        });
    }
}
