package org.activityinfo.ui.client.component.formdesigner.design;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.type.Cardinality;

/**
 * @author yuriyz on 7/17/14.
 */
public class EnumTypeFieldDesignWidget extends Composite implements FieldDesignWidget {

    private static OurUiBinder uiBinder = GWT
            .create(OurUiBinder.class);

    interface OurUiBinder extends UiBinder<Widget, EnumTypeFieldDesignWidget> {
    }

    @UiField(provided = true)
    ListBox cardinality;

    public EnumTypeFieldDesignWidget() {
        cardinality = createCardinalityListBox();
        initWidget(uiBinder.createAndBindUi(this));

        cardinality.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {

            }
        });
    }

    private static ListBox createCardinalityListBox() {
        final ListBox result = new ListBox(false);
        result.addItem(I18N.CONSTANTS.singleChoice(), Cardinality.SINGLE.name());
        result.addItem(I18N.CONSTANTS.multipleChoice(), Cardinality.MULTIPLE.name());
        return result;
    }

    @Override
    public Widget asWidget() {
        return this;
    }
}
