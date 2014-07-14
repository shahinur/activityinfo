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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.client.component.formdesigner.FormDesigner;
import org.activityinfo.ui.client.component.formdesigner.Metrics;

/**
 * @author yuriyz on 7/14/14.
 */
public class DropTargetPanel extends AbsolutePanel {

    private FormDesigner formDesigner;

    public DropTargetPanel() {
    }

    @Override
    public void insert(Widget w, int beforeIndex) {
        super.insert(w, beforeIndex);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                resizeDropPanel();
            }
        });
    }

    private void resizeDropPanel() {
        int panelHeight = getOffsetHeight();
        int actualHeight = 0;
        for (Widget child : SpacerDropController.getDropPanelChilds(this)) {
            actualHeight = actualHeight + child.getOffsetHeight();
        }

        //GWT.log("panelHeight=" + panelHeight + ", actualHeight=" + actualHeight);
        if ((panelHeight - Metrics.EXPECTED_MAX_CHILD_HEIGHT) < actualHeight) {
            int height = actualHeight + Metrics.PANEL_HEIGHT_INCREASE_CORRECTION;
            setHeight(height + "px");

            // increase also height of container panel
            formDesigner.getFormDesignerPanel().getContainerPanel().setHeight((height + Metrics.HEIGHT_DIFF_BETWEEN_DROPPANEL_AND_CONTAINER_ABSOLUTE_PANEL) + "px");
        }
    }

    public FormDesigner getFormDesigner() {
        return formDesigner;
    }

    public void setFormDesigner(FormDesigner formDesigner) {
        this.formDesigner = formDesigner;
    }
}
