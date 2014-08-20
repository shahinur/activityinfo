package org.activityinfo.ui.widget.form;
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

import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.DoubleBox;
import org.activityinfo.model.type.converter.CoordinateAxis;
import org.activityinfo.ui.style.BaseStyles;

/**
 * @author yuriyz on 1/27/14.
 */
public class CoordinateBox extends DoubleBox {


    @UiConstructor
    public CoordinateBox(CoordinateAxis axis) {
        super();
        addStyleName(BaseStyles.FORM_CONTROL.getClassNames());
        // todo
//        this.setPropertyEditor(editor);
//        this.setValidator(editor);
//        this.setValidateOnBlur(true);
    }

    /**
     * Sets the bounds for this field
     *
     * @param name     the name of the bounds <></>o present to users in the event of
     *                 violation, (e.g. "Kapisa Province Boundary"
     * @param minValue minimum allowed value for this field
     * @param maxValue maximum allowed value for this field
     */
    public void setBounds(String name, double minValue, double maxValue) {
//        editor.setMinValue(minValue - CoordinateField.DELTA);
//        editor.setMaxValue(maxValue + CoordinateField.DELTA);
//        editor.setOutOfBoundsMessage(I18N.MESSAGES.coordOutsideBounds(name));
    }
}
