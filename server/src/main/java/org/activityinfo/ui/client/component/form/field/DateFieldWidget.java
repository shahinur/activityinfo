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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import org.activityinfo.core.shared.type.formatter.DateFormatterFactory;
import org.activityinfo.model.type.FieldType;

import java.util.Date;

/**
 * A text box that shows a {@link DatePicker} when the user focuses on it.
 * <p/>
 * <h3>CSS Style Rules</h3>
 * <p/>
 * <dl>
 * <dt>.gwt-DateBox</dt>
 * <dd>default style name</dd>
 * <dt>.dateBoxPopup</dt>
 * <dd>Applied to the popup around the DatePicker</dd>
 * <dt>.dateBoxFormatError</dt>
 * <dd>Default style for when the date box has bad input. Applied by
 * {@link DateBox.DefaultFormat} when the text does not represent a date that
 * can be parsed</dd>
 * </dl>
 * <p/>
 * <p>
 * <h3>Example</h3>
 * {@example com.google.gwt.examples.DateBoxExample}
 * </p>
 */
public class DateFieldWidget implements FormFieldWidget<Date> {

    private final DateBox dateBox;
    private boolean readOnly;

    public DateFieldWidget(final ValueUpdater valueUpdater) {
        dateBox = new DateBox(new DatePicker(), null, createFormat()) {
            @Override
            public void showDatePicker() {
                if(!readOnly) {
                    super.showDatePicker();
                }
            }
        };
        dateBox.setStyleName("form-control");
        dateBox.getTextBox().setStyleName("form-control");
        dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                valueUpdater.update(event.getValue());
            }
        });
    }

    public static DateBox.Format createFormat() {
        return new DateBox.DefaultFormat(DateTimeFormat.getFormat(DateFormatterFactory.FORMAT));
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        this.dateBox.getTextBox().setReadOnly(readOnly);
    }

    @Override
    public void setValue(Date value) {
        dateBox.setValue(value);
    }

    @Override
    public void setType(FieldType type) {

    }

    @Override
    public Widget asWidget() {
        return dateBox;
    }
}

