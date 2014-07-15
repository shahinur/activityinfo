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
import com.google.common.base.Function;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.form.FormField;
import org.activityinfo.ui.client.component.form.field.FormFieldWidget;
import org.activityinfo.ui.client.component.formdesigner.FormDesigner;
import org.activityinfo.ui.client.component.formdesigner.Spacer;
import org.activityinfo.ui.client.component.formdesigner.container.FieldWidgetContainer;
import org.activityinfo.ui.client.component.formdesigner.palette.FieldLabel;
import org.activityinfo.ui.client.component.formdesigner.palette.FieldTemplate;

import javax.annotation.Nullable;

/**
 * @author yuriyz on 07/07/2014.
 */
public class DropPanelDropController extends AbsolutePositionDropController {

    private final Spacer spacer = new Spacer();
    private FormDesigner formDesigner;
    private DropTargetPanel dropTarget;

    public DropPanelDropController(DropTargetPanel dropTarget, FormDesigner formDesigner) {
        super(dropTarget);
        this.formDesigner = formDesigner;
        this.dropTarget = dropTarget;
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {

        int spacerIndex = dropTarget.getWidgetIndex(spacer);
        if (spacerIndex != -1) {
            dropTarget.remove(spacerIndex);
        }

        if (context.draggable instanceof FieldLabel) {
            previewDropNewWidget(((FieldLabel) context.draggable).getFieldTemplate());
        } else {
            draggingExistingWidgetContainer(context);
        }
    }

    private void previewDropNewWidget(FieldTemplate fieldTemplate) throws VetoDragException {
        final FormField formField = fieldTemplate.createField();
        formDesigner.getFormClass().insertElement(formDesigner.getInsertIndex(), formField);

        formDesigner.getFormFieldWidgetFactory()
                .createWidget(formField, NullValueUpdater.INSTANCE).then(new Function<FormFieldWidget, Void>() {
            @Nullable
            @Override
            public Void apply(@Nullable FormFieldWidget formFieldWidget) {
                Widget containerWidget = new FieldWidgetContainer(formDesigner, formFieldWidget, formField).asWidget();
                Integer insertIndex = formDesigner.getInsertIndex();
                if (insertIndex != null) {
                    dropTarget.insert(containerWidget, insertIndex);
                } else { // null means insert in tail
                    dropTarget.add(containerWidget);
                }
                return null;
            }
        });


        // forbid drop of source control widget
        throw new VetoDragException();
    }

    private void draggingExistingWidgetContainer(final DragContext context) throws VetoDragException {

        final Integer insertIndex = formDesigner.getInsertIndex();
        final Widget draggable = context.draggable;
        if (insertIndex != null) {
            dropTarget.insert(draggable, insertIndex);
        } else { // null means insert in tail
            dropTarget.add(draggable);
        }

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                // avoid widgets overlap
                draggable.getElement().getStyle().setPosition(Style.Position.RELATIVE);
                draggable.getElement().getStyle().setTop(0, Style.Unit.PX);
                formDesigner.getDragController().makeNotDraggable(draggable);
            }
        });
        throw new VetoDragException();
    }

}
