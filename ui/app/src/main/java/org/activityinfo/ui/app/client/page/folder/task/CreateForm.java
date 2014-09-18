package org.activityinfo.ui.app.client.page.folder.task;
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

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.action.CreateDraft;
import org.activityinfo.ui.app.client.page.form.FormPlace;
import org.activityinfo.ui.app.client.page.form.FormViewType;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;

/**
 * @author yuriyz on 9/18/14.
 */
public class CreateForm implements Task {

    private Application application;
    private ResourceId ownerId;

    public CreateForm(Application application, ResourceId ownerId) {
        this.application = application;
        this.ownerId = ownerId;
    }

    @Override
    public String getLabel() {
        return I18N.CONSTANTS.newForm();
    }

    @Override
    public Icon getIcon() {
        return FontAwesome.FILE;
    }

    @Override
    public void onClicked() {
        FormClass newFormClass = new FormClass(Resources.generateId());
        newFormClass.setLabel("New form");
        newFormClass.setOwnerId(ownerId);

        application.getDispatcher().dispatch(new CreateDraft(newFormClass.asResource()));
        application.getRouter().navigate(new FormPlace(newFormClass.getId(), FormViewType.DESIGN));
    }
}
