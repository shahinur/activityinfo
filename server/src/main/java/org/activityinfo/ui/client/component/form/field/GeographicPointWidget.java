package org.activityinfo.ui.client.component.form.field;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.shared.model.AiLatLng;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.ui.client.widget.coord.CoordinateBox;

/**
 * @author yuriyz on 1/31/14.
 */
public class GeographicPointWidget implements FormFieldWidget<AiLatLng> {

    interface GeographicPointWidgetUiBinder extends UiBinder<HTMLPanel, GeographicPointWidget> {
    }

    private static GeographicPointWidgetUiBinder ourUiBinder = GWT.create(GeographicPointWidgetUiBinder.class);

    private final HTMLPanel panel;

    @UiField
    CoordinateBox latitudeBox;

    @UiField
    CoordinateBox longitudeBox;

    public GeographicPointWidget(final ValueUpdater valueUpdater) {
        panel = ourUiBinder.createAndBindUi(this);

        ValueChangeHandler<Double> handler = new ValueChangeHandler<Double>() {
            @Override
            public void onValueChange(ValueChangeEvent<Double> event) {
                valueUpdater.update(getValue());
            }
        };
        latitudeBox.addValueChangeHandler(handler);
        longitudeBox.addValueChangeHandler(handler);
    }

    private AiLatLng getValue() {
        Double latitude = latitudeBox.getValue();
        Double longitude = longitudeBox.getValue();
        if(latitude == null || longitude == null) {
            return null;
        } else {
            return new AiLatLng(latitude, longitude);
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        latitudeBox.setReadOnly(readOnly);
        longitudeBox.setReadOnly(readOnly);
    }

    @Override
    public void setValue(AiLatLng value) {
        if (value != null) {
            latitudeBox.setValue(value.getLat());
            longitudeBox.setValue(value.getLng());
        } else {
            latitudeBox.setValue(null);
            longitudeBox.setValue(null);
        }
    }

    @Override
    public void setType(FieldType type) {

    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
