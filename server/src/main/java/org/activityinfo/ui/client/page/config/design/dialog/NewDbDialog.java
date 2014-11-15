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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.ui.client.widget.dialog.WizardDialog;

/**
 * @author yuriyz on 11/13/2014.
 */
public class NewDbDialog {

    private final Dispatcher dispatcher;
    private final WizardDialog dialog;
    private final NewDbDialogData dialogData = new NewDbDialogData();

    public NewDbDialog(final Dispatcher dispatcher) {
        this.dispatcher = dispatcher;

        this.dialog = new WizardDialog(new NewDbPageSwitcher(dispatcher, dialogData));
        this.dialog.setTitle(I18N.CONSTANTS.createNewDatabase());
        this.dialog.setOnFinishHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createDatabase();
            }
        });
    }

    private void createDatabase() {
        // todo
        dialog.getDialog().getPrimaryButton().setText(I18N.CONSTANTS.creating());
        dialog.hide();
    }

    public NewDbDialog show() {
        dialog.show();
        return this;
    }
}
