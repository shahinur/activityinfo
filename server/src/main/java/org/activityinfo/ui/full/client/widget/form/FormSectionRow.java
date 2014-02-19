package org.activityinfo.ui.full.client.widget.form;
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
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.shared.form.FormSection;
import org.activityinfo.ui.full.client.style.TransitionUtil;

/**
 * @author yuriyz on 2/18/14.
 */
public class FormSectionRow extends Composite {

    private static FormSectionRowUiBinder uiBinder = GWT
            .create(FormSectionRowUiBinder.class);

    interface FormSectionRowUiBinder extends UiBinder<Widget, FormSectionRow> {
    }

    private final FormSection formSection;
    private final FormPanel formPanel;

    @UiField
    HTML label;
    @UiField
    RowToolbar toolbar;

    public FormSectionRow(FormSection formSection, FormPanel formPanel) {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));

        this.formSection = formSection;
        this.formPanel = formPanel;
        this.toolbar.attach(this);
        this.toolbar.setFormPanel(formPanel);
        this.label.setHTML(SafeHtmlUtils.fromSafeConstant(formSection.getLabel().getValue()));
    }

    public FormSection getFormSection() {
        return formSection;
    }

    public FormPanel getFormPanel() {
        return formPanel;
    }
}
