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
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.ui.full.client.style.TransitionUtil;
import org.activityinfo.ui.full.client.util.GwtUtil;
import org.activityinfo.ui.full.client.widget.CompositeWithMirror;
import org.activityinfo.ui.full.client.widget.FormFieldTypeCombobox;
import org.activityinfo.ui.full.client.widget.dialog.ChangeFormFieldTypeDialog;

import javax.annotation.Nonnull;

/**
 * @author yuriyz on 2/26/14.
 */
public class FormFieldInlineEdit extends CompositeWithMirror {

    private static FormFieldInlineEditBinder uiBinder = GWT
            .create(FormFieldInlineEditBinder.class);

    interface FormFieldInlineEditBinder extends UiBinder<Widget, FormFieldInlineEdit> {
    }

    private FormField formField;
    private boolean editMode = false;
    private FormFieldRow row;

    @UiField
    TextBox label;
    @UiField
    TextArea description;
    @UiField
    TextBox unit;
    @UiField
    FormFieldTypeCombobox type;
    @UiField
    CheckBox required;
    @UiField
    Button okButton;
    @UiField
    Button cancelButton;
    @UiField
    Button changeButton;
    @UiField
    DivElement unitContainer;
    @UiField
    FormFieldInlineReferenceEdit referencePanel;
    @UiField
    DivElement referenceContainer;

    public FormFieldInlineEdit() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
        setUnitControlState();
        setReferencePanelState();
        referencePanel.setContainer(this);
    }

    public void setUnitControlState() {
        final boolean isUnitVisible = type.getSelectedType() == FormFieldType.QUANTITY;
        GwtUtil.setVisible(unitContainer, isUnitVisible);
    }

    public void setReferencePanelState() {
        GwtUtil.setVisible(referenceContainer, type.getSelectedType() == FormFieldType.REFERENCE);
        referencePanel.apply();
    }

    @UiHandler("okButton")
    public void onOk(ClickEvent event) {
        hide();
    }

    @UiHandler("cancelButton")
    public void cancelButton(ClickEvent event) {
        hide();
    }

    @UiHandler("changeButton")
    public void changeButton(ClickEvent event) {
        final ChangeFormFieldTypeDialog dialog = new ChangeFormFieldTypeDialog(formField);
        dialog.show();
        dialog.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FormFieldInlineEdit.this.type.setSelectedType(dialog.getType().getSelectedType());
            }
        });
    }

    public void hide() {
        setVisible(false);
    }

    public void applyNew(Element... mirrorElements) {
        final Cuid newCuid = CuidAdapter.newFormField();
        final FormField newFormField = new FormField(newCuid);
        newFormField.setType(FormFieldType.FREE_TEXT);
        apply(newFormField, mirrorElements);
    }

    public void apply(@Nonnull FormField formField, Element... mirrorElements) {
        setFormField(formField);
        setMirrorElements(mirrorElements);
        apply();
    }

    private void apply() {
        label.setValue(formField.getLabel().getValue());
        description.setValue(formField.getDescription().getValue());
        unit.setValue(formField.getUnit().getValue());
        type.setSelectedType(formField.getType());
        required.setValue(formField.isRequired());
        setUnitControlState();
        setChangeButtonState();
        setReferencePanelState();
    }

    public void updateModel() {
        formField.setLabel(new LocalizedString(label.getValue()));
        formField.setType(type.getSelectedType());
        formField.setDescription(new LocalizedString(description.getValue()));
        formField.setUnit(new LocalizedString(unit.getValue()));
        formField.setRequired(required.getValue());
    }

    public FormField getFormField() {
        return formField;
    }

    public void setFormField(FormField formField) {
        this.formField = formField;
    }

    public Button getOkButton() {
        return okButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        this.type.setEnabled(!editMode);
        GwtUtil.setVisibleInline(editMode, changeButton.getElement());
        setChangeButtonState();
    }

    private void setChangeButtonState() {
        changeButton.setEnabled(formField != null && !formField.getType().getAllowedConvertTo().isEmpty());
    }

    public FormFieldRow getRow() {
        return row;
    }

    public void setRow(FormFieldRow row) {
        this.row = row;
    }
}