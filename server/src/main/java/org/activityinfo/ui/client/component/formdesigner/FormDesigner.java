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
import org.activityinfo.ui.client.component.formdesigner.drop.DropPanelDropController;
import org.activityinfo.ui.client.component.formdesigner.drop.ForwardDropController;
import org.activityinfo.ui.client.component.formdesigner.drop.SpacerDropController;
import org.activityinfo.ui.client.component.formdesigner.header.HeaderPresenter;
import org.activityinfo.ui.client.component.formdesigner.properties.PropertiesPresenter;

import javax.annotation.Nonnull;

/**
 * @author yuriyz on 07/07/2014.
 */
public class FormDesigner {

    private final ControlBucketBuilder controlBucketBuilder;
    private final PickupDragController dragController;
    private final EventBus eventBus = new SimpleEventBus();
    private final ResourceLocator resourceLocator;
    private final FormClass formClass;
    private final PropertiesPresenter propertiesPresenter;
    private final HeaderPresenter headerPresenter;
    private final FormDesignerPanel formDesignerPanel;
    private Integer insertIndex = null; // null means insert in tail

    public FormDesigner(@Nonnull FormDesignerPanel formDesignerPanel, @Nonnull ResourceLocator resourceLocator, @Nonnull FormClass formClass) {
        this.formDesignerPanel = formDesignerPanel;
        this.resourceLocator = resourceLocator;
        this.formClass = formClass;

        propertiesPresenter = new PropertiesPresenter(formDesignerPanel.getPropertiesPanel(), eventBus);

        dragController = new PickupDragController(formDesignerPanel.getContainerPanel(), false);
        dragController.setBehaviorMultipleSelection(false);

        controlBucketBuilder = new ControlBucketBuilder(formDesignerPanel.getControlBucket(), dragController);
        controlBucketBuilder.build();

        dragController.addDragHandler(new ControlDragHandler());

        ForwardDropController forwardDropController = new ForwardDropController(formDesignerPanel.getDropPanel());
        forwardDropController.add(new DropPanelDropController(formDesignerPanel.getDropPanel(), this));
        forwardDropController.add(new SpacerDropController(formDesignerPanel.getDropPanel(), this));

        dragController.registerDropController(forwardDropController);

        headerPresenter = new HeaderPresenter(this);
        headerPresenter.show();
    }

    public ControlType getControlType(Widget widget) {
        return controlBucketBuilder.getControlMap().inverse().get(widget);
    }

    public FormDesignerPanel getFormDesignerPanel() {
        return formDesignerPanel;
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
