package org.activityinfo.ui.client.component.formdesigner;
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

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author yuriyz on 07/04/2014.
 */
public class FormDesignerPanel extends Composite {

    private static OurUiBinder uiBinder = GWT
            .create(OurUiBinder.class);

    interface OurUiBinder extends UiBinder<Widget, FormDesignerPanel> {
    }

    private final PickupDragController dragController;

    @UiField
    AbsolutePanel absolutePanel;
    @UiField
    Button section;
    @UiField
    Button dropDownField;
    @UiField
    Button numberField;
    @UiField
    Button multiTextField;
    @UiField
    Button singleTextField;

    public FormDesignerPanel() {
        FormDesignerStyles.INSTANCE.ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));
        dragController = new PickupDragController(absolutePanel, false);
        dragController.setBehaviorMultipleSelection(false);

        dragController.makeDraggable(section);
        dragController.makeDraggable(dropDownField);
        dragController.makeDraggable(numberField);
        dragController.makeDraggable(multiTextField);
        dragController.makeDraggable(singleTextField);
    }
}
