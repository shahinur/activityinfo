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

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.activityinfo.ui.client.component.form.field.TextFieldWidget;
import org.activityinfo.ui.client.component.formdesigner.WidgetContainer;
import org.activityinfo.ui.client.widget.TextBox;

/**
 * @author yuriyz on 07/07/2014.
 */
public class SingleLineDropHandler implements DropHandler {

    private final EventBus eventBus;

    public SingleLineDropHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Drop drop(AbsolutePanel dropTarget, ValueUpdater valueUpdater) {
        final TextBox box = new TextBox();
        dropTarget.add(new WidgetContainer(eventBus, new TextFieldWidget(valueUpdater)).asWidget());

        final Drop drop = new Drop();
        drop.setDropHeight(box.getOffsetHeight());
        return drop;
    }
}
