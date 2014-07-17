package org.activityinfo.ui.client.local.ui;

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

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.local.capability.LocalCapabilityProfile;
import org.activityinfo.ui.client.local.capability.ProfileResources;

public class BasePromptDialog extends Dialog {

    static {
        ProfileResources.INSTANCE.style().ensureInjected();
    }

    protected final static LocalCapabilityProfile CAPABILITY_PROFILE = GWT.create(LocalCapabilityProfile.class);

    public BasePromptDialog(String html) {

        setWidth(500);
        setHeight(350);
        setHeadingText(I18N.CONSTANTS.installOffline());
        setModal(true);
        setLayout(new FitLayout());

        Html bodyHtml = new Html(html);
        bodyHtml.addStyleName(ProfileResources.INSTANCE.style().startupDialogBody());

        add(bodyHtml);
    }
}
