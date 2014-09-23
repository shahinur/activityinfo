package org.activityinfo.ui.app.client.chrome;
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
import org.activityinfo.ui.style.*;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.t;

/**
 * @author yuriyz on 9/22/14.
 */
public class EditLabelDialog extends VComponent {

    private final Modal modal = new Modal();
    private final InputControl inputControl;

    private ClickHandler okClickHandler;
    private String label;
    private Alert failedAlert = new Alert(AlertStyle.DANGER,
            H.h5("Oops... You encounter bug. Failed to edit label."));

    public EditLabelDialog() {
        Button cancelButton = new Button(ButtonStyle.DEFAULT, t(I18N.CONSTANTS.cancel()));
        cancelButton.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                modal.setVisible(false);
            }
        });

        Button okButton = new Button(ButtonStyle.PRIMARY, t(I18N.CONSTANTS.ok()));
        okButton.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                if (okClickHandler != null) {
                    okClickHandler.onClicked();
                }
            }
        });

        inputControl = new InputControl(InputControlType.TEXT, "", "");
        failedAlert.setVisible(false);

        HorizontalForm form = new HorizontalForm().
                addGroup(Forms.label("Name"), inputControl).
                addGroup(Forms.label(BaseStyles.COL_SM_1, ""), failedAlert);

        modal.setTitle(t("Edit"));
        modal.setBody(form);
        modal.setFooter(okButton, cancelButton);
    }

    @Override
    protected void componentDidMount() {
        super.componentDidMount();
        inputControl.setValue(label); // set value after component is mounted
    }

    @Override
    protected VTree render() {
        return modal;
    }

    public InputControl getInputControl() {
        return inputControl;
    }

    public void setVisible(boolean visible) {
        modal.setVisible(visible);
    }

    public Button createLinkButton() {
        Button button = new Button(ButtonStyle.LINK, FontAwesome.EDIT.render());
        button.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                EditLabelDialog.this.setVisible(true);
            }
        });
        return button;
    }

    public void setOkClickHandler(ClickHandler okClickHandler) {
        this.okClickHandler = okClickHandler;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void failedToEditLabel() {
        failedAlert.setVisible(true);
    }
}
