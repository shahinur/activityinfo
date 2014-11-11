package org.activityinfo.ui.client.widget.form;
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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;

/**
 * @author yuriyz on 11/10/2014.
 */
public class FormGroup extends Composite implements HasWidgets {

    private static OurUiBinder uiBinder = GWT.create(OurUiBinder.class);

    interface OurUiBinder extends UiBinder<Widget, FormGroup> {
    }

    @UiField
    LabelElement label;
    @UiField
    HTMLPanel widget;
    @UiField
    HTMLPanel messageContainer;
    @UiField
    SpanElement validationMessage;
    @UiField
    SpanElement validationMargin;
    @UiField
    SpanElement description;
    @UiField
    SpanElement descriptionMargin;

    private boolean skipWidgetColStyle = false;

    @UiConstructor
    public FormGroup() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public FormGroup label(String label) {
        this.label.setInnerHTML(SafeHtmlUtils.fromString(label).asString());
        return this;
    }

    public void setDescription(String description) {
        this.description.addClassName("help-block"); // add help-block dynamically, we don't want div to take space if description is not set
        this.description.setInnerHTML(SafeHtmlUtils.fromString(description).asString());
    }

    public FormGroup columnLabelWidth(final int width) {
        if (width < 0 || width > 12) {
            throw new IllegalArgumentException("Bootstrap width is invalid. Must be in range of 0..12. Width: " + width);
        }

        this.label.addClassName(GridCol.col(width));

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if (!skipWidgetColStyle) {
                    FormGroup.this.widget.addStyleName(GridCol.remainingCol(width));
                }
            }
        });

        this.descriptionMargin.addClassName(GridCol.col(width));
        this.description.addClassName(GridCol.remainingCol(width));

        this.validationMargin.addClassName(GridCol.col(width));
        this.validationMessage.addClassName(GridCol.remainingCol(width));
        return this;
    }

    public FormGroup validationMessage(String validationMessage) {
        this.validationMessage.setInnerHTML(SafeHtmlUtils.fromString(validationMessage).asString());
        return this;
    }

    public FormGroup showValidationMessage(String validationMessage) {
        return validationMessage(validationMessage).showValidationMessage(true);
    }

    public FormGroup showValidationMessage(boolean show) {

        if (show) {
            validationMessage.addClassName("help-block");
        } else {
            validationMessage.removeClassName("help-block");
        }

        messageContainer.setVisible(show);
        return this;
    }

    public FormGroup validationStateType(ValidationStateType stateType) {
        // reset
        messageContainer.removeStyleName("has-error");
        messageContainer.removeStyleName("has-warning");
        messageContainer.removeStyleName("has-success");

        // set style
        switch (stateType) {
            case ERROR:
                messageContainer.addStyleName("has-error");
                break;
            case SUCCESS:
                messageContainer.addStyleName("has-success");
                break;
            case WARNING:
                messageContainer.addStyleName("has-warning");
                break;
        }
        return this;
    }

    public static FormGroup newInstance() {
        return new FormGroup();
    }

    @Override
    public void add(Widget w) {
        widget.add(w);
    }

    @Override
    public void clear() {
        widget.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        return widget.iterator();
    }

    @Override
    public boolean remove(Widget w) {
        return widget.remove(w);
    }

    public void setValidationMessage(String validationMessage) { // method for UiBinder
        validationMessage(validationMessage);
    }

    public void setValidationStateType(String type) {  // method for UiBinder
        validationStateType(ValidationStateType.valueOf(type));
    }

    public void setShowValidationMessage(boolean show) { // method for UiBinder
        showValidationMessage(show);
    }

    public void setLabel(String label) {  // method for UiBinder
        label(label);
    }

    public void setColumnLabelWidth(int width) {  // method for UiBinder
        columnLabelWidth(width);
    }

    public void setColumnWidgetWidth(int width) {
        this.widget.addStyleName(GridCol.col(width));
        skipWidgetColStyle = true;
    }

}
