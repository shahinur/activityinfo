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

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.activityinfo.ui.client.component.formdesigner.drop.Drop;
import org.activityinfo.ui.client.component.formdesigner.drop.DropHandlerFactory;

/**
 * @author yuriyz on 07/07/2014.
 */
public class DropPanelDropController extends AbsolutePositionDropController {

    private FormDesigner formDesigner;
    private AbsolutePanel dropTarget;

    public DropPanelDropController(AbsolutePanel dropTarget, FormDesigner formDesigner) {
        super(dropTarget);
        this.formDesigner = formDesigner;
        this.dropTarget = dropTarget;
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
        int dropPanelHeightBeforeDrop = dropTarget.getOffsetHeight();

        final ValueUpdater valueUpdater = new ValueUpdater() {
            @Override
            public void update(Object value) {
                // todo
            }
        };

        ControlType controlType = formDesigner.getControlType(context.draggable);
        Drop drop = new DropHandlerFactory(formDesigner.getEventBus()).create(controlType).drop(dropTarget, valueUpdater);

        resizeDropPanel(dropPanelHeightBeforeDrop, drop);

        // forbid drop of source control widget
        throw new VetoDragException();
    }

    private void resizeDropPanel(int dropPanelHeightBeforeDrop, Drop drop) {
        // todo !!!
    }
}
