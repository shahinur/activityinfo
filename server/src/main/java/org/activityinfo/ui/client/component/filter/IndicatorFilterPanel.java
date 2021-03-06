package org.activityinfo.ui.client.component.filter;

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

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;

public class IndicatorFilterPanel extends IndicatorTreePanel implements FilterPanel {

    private static final int UPDATE_DELAY = 100;

    private Timer delayedEvent;


    public IndicatorFilterPanel(Dispatcher service, boolean multipleSelection) {
        super(service, multipleSelection);


        addCheckChangedListener(new Listener<TreePanelEvent>() {
            @Override
            public void handleEvent(TreePanelEvent be) {
                // aggregate events before re-throwing, to avoid cascading TreePanelEvents
                delayedEvent.cancel();
                delayedEvent.schedule(UPDATE_DELAY);
            }
        });

        delayedEvent = new Timer() {
            @Override
            public void run() {
                ValueChangeEvent.fire(IndicatorFilterPanel.this, getValue());
            }
        };
    }

    @Override
    public Filter getValue() {
        Filter filter = new Filter();
        filter.addRestriction(DimensionType.Indicator, getSelectedIds());

        return filter;
    }

    @Override
    public void setValue(Filter value) {
        setSelection(value.getRestrictions(DimensionType.Indicator));
    }

    @Override
    public void setValue(Filter value, boolean fireEvents) {
        setSelection(value.getRestrictions(DimensionType.Indicator));
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Filter> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void applyBaseFilter(Filter filter) {
        // we don't filter indicators
    }
}
