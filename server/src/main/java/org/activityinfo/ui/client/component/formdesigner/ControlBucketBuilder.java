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

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;

import java.util.Collections;
import java.util.Map;

/**
 * @author yuriyz on 07/07/2014.
 */
public class ControlBucketBuilder {

    private final AbsolutePanel bucketPanel;
    private final Map<ControlType, Button> controlMap = Maps.newHashMap();
    private final DragController dragController;

    public ControlBucketBuilder(AbsolutePanel bucketPanel, DragController dragController) {
        this.bucketPanel = bucketPanel;
        this.dragController = dragController;
    }

    public void build() {
        final Map<Integer, Integer> top = Maps.newHashMap();

        // support only 2 columns for now
        top.put(0, 0);
        top.put(1, 0);

        for (ControlType type : ControlType.values()) {
            int left = Metrics.SOURCE_CONTROL_INITIAL_LEFT + Metrics.SOURCE_CONTROL_WIDTH_PX * type.getColumn() +
                    + (type.getColumn() == 0 ? 0 : Metrics.SOURCE_CONTROL_MARGIN_RIGHT);
            Integer topValue = top.get(type.getColumn());
            bucketPanel.add(createButton(type), left, topValue);

            topValue = topValue + Metrics.SOURCE_CONTROL_HEIGHT_PX;
            top.put(type.getColumn(), topValue);
        }

        bucketPanel.setHeight(Collections.max(top.values()) + "px");
    }

    private Button createButton(ControlType type) {
        final Button button = new Button(type.getLabel());
        button.setStyleName("btn btn-primary btn-xs btn-block control");
        button.setWidth(Metrics.SOURCE_CONTROL_WIDTH_PX + "px");
        controlMap.put(type, button);
        dragController.makeDraggable(button);
        return button;
    }

    public Map<ControlType, Button> getControlMap() {
        return controlMap;
    }
}
