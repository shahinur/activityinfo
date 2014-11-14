package org.activityinfo.ui.client.page.config.design.dialog;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.widget.CheckBox;
import org.activityinfo.ui.client.widget.dialog.WizardDialog;
import org.activityinfo.ui.client.widget.dialog.WizardPageAdapter;

/**
 * @author yuriyz on 11/13/2014.
 */
public class NewDbDetailsPage extends WizardPageAdapter {

    private static OurUiBinder uiBinder = GWT.create(OurUiBinder.class);

    interface OurUiBinder extends UiBinder<Widget, NewDbDetailsPage> {
    }

    private Widget rootPanel;

    @UiField
    HTMLPanel optionsContainer;
    @UiField
    CheckBox copyUserPermissions;
    @UiField
    CheckBox copyPartners;
    @UiField
    CheckBox copyData;

    public NewDbDetailsPage() {
         rootPanel = uiBinder.createAndBindUi(this);
    }

    public NewDbDetailsPage showCopyOptions(boolean show) {
        optionsContainer.setVisible(show);
        return this;
    }

    @Override
    public void onShow(WizardDialog wizardDialog) {
        super.onShow(wizardDialog);
        wizardDialog.getDialog().getPrimaryButton().setText(I18N.CONSTANTS.create());
    }

    @Override
    public IsWidget asWidget() {
        return rootPanel;
    }
}
