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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.client.component.formdesigner.drop.DropPanelDropController;
import org.activityinfo.ui.client.component.formdesigner.drop.ForwardDropController;
import org.activityinfo.ui.client.component.formdesigner.drop.SpacerDropController;
import org.activityinfo.ui.client.component.formdesigner.event.WidgetContainerSelectionEvent;
import org.activityinfo.ui.client.component.formdesigner.properties.PropertiesPresenter;

/**
 * @author yuriyz on 07/07/2014.
 */
public class FormDesigner {

    private final ControlBucketBuilder controlBucketBuilder;
    private final PickupDragController dragController;
    private final EventBus eventBus = new SimpleEventBus();
    private final ResourceLocator resourceLocator;
    private final FormClass formClass = new FormClass(ResourceId.generateId());
    private final PropertiesPresenter propertiesPresenter;
    private Integer insertIndex = null; // null means insert in tail

    public FormDesigner(FormDesignerPanel formDesignerPanel, ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;

        propertiesPresenter = new PropertiesPresenter(formDesignerPanel.getPropertiesPanel());

        dragController = new PickupDragController(formDesignerPanel.getContainerPanel(), false);
        dragController.setBehaviorMultipleSelection(false);

        controlBucketBuilder = new ControlBucketBuilder(formDesignerPanel.getControlBucket(), dragController);
        controlBucketBuilder.build();

        dragController.addDragHandler(new ControlDragHandler());

        ForwardDropController forwardDropController = new ForwardDropController(formDesignerPanel.getDropPanel());
        forwardDropController.add(new DropPanelDropController(formDesignerPanel.getDropPanel(), this));
        forwardDropController.add(new SpacerDropController(formDesignerPanel.getDropPanel(), this));

        dragController.registerDropController(forwardDropController);
        eventBus.addHandler(WidgetContainerSelectionEvent.TYPE, new WidgetContainerSelectionEvent.Handler() {
            @Override
            public void handle(WidgetContainerSelectionEvent event) {
                onContainerSelection(event.getSelectedItem());
            }
        });
    }

    private void onContainerSelection(WidgetContainer selectedItem) {
        propertiesPresenter.show(selectedItem);
    }

    public ControlType getControlType(Widget widget) {
        return controlBucketBuilder.getControlMap().inverse().get(widget);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }

    public PickupDragController getDragController() {
        return dragController;
    }

    public FormClass getFormClass() {
        return formClass;
    }

    public Integer getInsertIndex() {
        return insertIndex;
    }

    public void setInsertIndex(Integer insertIndex) {
        this.insertIndex = insertIndex;
    }
}
