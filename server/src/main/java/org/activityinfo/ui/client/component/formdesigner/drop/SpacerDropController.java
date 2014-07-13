package org.activityinfo.ui.client.component.formdesigner.drop;
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
import com.google.common.collect.Lists;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.client.component.formdesigner.FormDesigner;
import org.activityinfo.ui.client.component.formdesigner.Metrics;
import org.activityinfo.ui.client.component.formdesigner.Spacer;

import java.util.List;

/**
 * @author yuriyz on 7/8/14.
 */
public class SpacerDropController extends AbsolutePositionDropController {

    private final Spacer spacer = new Spacer();
    private FormDesigner formDesigner;
    private AbsolutePanel dropTarget;

    public SpacerDropController(AbsolutePanel dropTarget, FormDesigner formDesigner) {
        super(dropTarget);
        this.formDesigner = formDesigner;
        this.dropTarget = dropTarget;
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
        removeSpacer();
    }

    @Override
    public void onLeave(DragContext context) {
        super.onLeave(context);
        removeSpacer();
    }

    private void removeSpacer() {
        int spacerIndex = dropTarget.getWidgetIndex(spacer);
        if (spacerIndex != -1) {
            dropTarget.remove(spacerIndex);
        }
    }


    @Override
    public void onMove(DragContext context) {
        super.onMove(context);
        Integer beforeIndex = calcSpacerIndex(context.desiredDraggableY);
        formDesigner.setInsertIndex(beforeIndex);
        if (beforeIndex != null) {
            dropTarget.insert(spacer, beforeIndex);
        } else {
            // null means add to tail
            dropTarget.add(spacer);
        }
    }

    private Integer calcSpacerIndex(int desiredDraggableY) {

        List<Widget> dropPanelChilds = getDropPanelChilds(dropTarget);
        int childCount = dropPanelChilds.size();

        if (childCount > 0 && desiredDraggableY > 0) {
            int i = 0;

            for (Widget widget : dropPanelChilds) {
                int minY = widget.getAbsoluteTop() - Metrics.MIN_Y_SPACER_CORRECTION;
                int maxY = minY + widget.getOffsetHeight() + Metrics.MAX_Y_SPACER_CORRECTION;

//                GWT.log("i=" + i + ", minY= " + minY + ", desiredDraggableY =" + desiredDraggableY);
//                GWT.log("i=" + i + ", maxY= " + maxY + ", desiredDraggableY =" + desiredDraggableY);

                if (desiredDraggableY > minY && desiredDraggableY < maxY) {
                    return i;
                }

                // if last widget then drop below last widget
                if (i == (childCount - 1) && desiredDraggableY > maxY) {
                    return null; // return null which means insert in tail
                }
                i++;
            }
        }
        return 0;
    }

    public static List<Widget> getDropPanelChilds(AbsolutePanel dropTarget) {
        List<Widget> widget = Lists.newArrayList();
        for (int i = 0; i < dropTarget.getWidgetCount(); i++) {
            Widget w = dropTarget.getWidget(i);
            if (w.getStyleName().contains("dragdrop-positioner") || w.getStyleName().contains("spacer")) {
                continue; // skip DnD positioner widgets
            }
            widget.add(w);
        }
        return widget;
    }
}
