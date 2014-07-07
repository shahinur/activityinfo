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

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;

/**
 * @author yuriyz on 07/07/2014.
 */
public class ControlDnDHandler implements DragHandler {

    @Override
    public void onDragEnd(DragEndEvent event) {
        event.toString();
    }

    @Override
    public void onDragStart(DragStartEvent event) {
        Object source = event.getSource();
//        if (source instanceof Widget) {
//            Widget dragObject = (Widget) source;
//            final HTML reserveSpace = new HTML();
//            reserveSpace.setHeight(dragObject.getElement().getStyle().getHeight());
//            reserveSpace.setWidth(dragObject.getElement().getStyle().getWidth());
//            reserveSpace.addStyleName(FormDesignerStyles.INSTANCE.controlOnDragSpacer());
//            if (dragObject.getParent() instanceof VerticalPanel) {
//
//            }
//        }
    }

    @Override
    public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
        event.toString();
    }

    @Override
    public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
        event.toString();
    }
}
