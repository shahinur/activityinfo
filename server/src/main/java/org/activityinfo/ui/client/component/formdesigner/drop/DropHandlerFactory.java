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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.activityinfo.ui.client.component.formdesigner.ControlType;

/**
 * @author yuriyz on 07/07/2014.
 */
public class DropHandlerFactory {

    private static final DropHandler DUMMY_DROP_HANDLER = new DropHandler() {
        @Override
        public Drop drop(AbsolutePanel dropTarget, ValueUpdater valueUpdater) {
            return new Drop();
        }
    };

    private final EventBus eventBus;

    public DropHandlerFactory(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public DropHandler create(ControlType type) {
        if (type != null) {
            switch (type) {
                case SINGLE_LINE_TEXT:
                    return new SingleLineDropHandler(eventBus);
            }
        }
        GWT.log("Control is not supported, type: " + type);
        return new SingleLineDropHandler(eventBus);
    }
}
