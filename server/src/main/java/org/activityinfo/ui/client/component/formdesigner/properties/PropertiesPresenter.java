package org.activityinfo.ui.client.component.formdesigner.properties;
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

import com.google.common.base.Strings;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.ParametrizedFieldType;
import org.activityinfo.ui.client.component.form.SimpleFormPanel;
import org.activityinfo.ui.client.component.form.VerticalFieldContainer;
import org.activityinfo.ui.client.component.form.field.FormFieldWidgetFactory;
import org.activityinfo.ui.client.component.formdesigner.container.FieldWidgetContainer;
import org.activityinfo.ui.client.component.formdesigner.container.WidgetContainer;
import org.activityinfo.ui.client.component.formdesigner.event.HeaderSelectionEvent;
import org.activityinfo.ui.client.component.formdesigner.event.WidgetContainerSelectionEvent;
import org.activityinfo.ui.client.component.formdesigner.header.HeaderPresenter;
import org.activityinfo.ui.client.component.formdesigner.skip.SkipDialog;

/**
 * @author yuriyz on 7/9/14.
 */
public class PropertiesPresenter {

    private final PropertiesPanel view;

    private SimpleFormPanel currentDesignWidget = null;
    private HandlerRegistration labelKeyUpHandler;
    private HandlerRegistration descriptionKeyUpHandler;
    private HandlerRegistration requiredValueChangeHandler;
    private HandlerRegistration readonlyValueChangeHandler;
    private HandlerRegistration skipButtonClickHandler;

    public PropertiesPresenter(PropertiesPanel view, EventBus eventBus) {
        this.view = view;
        eventBus.addHandler(WidgetContainerSelectionEvent.TYPE, new WidgetContainerSelectionEvent.Handler() {
            @Override
            public void handle(WidgetContainerSelectionEvent event) {
                WidgetContainer widgetContainer = event.getSelectedItem();
                if (widgetContainer instanceof FieldWidgetContainer) {
                    show((FieldWidgetContainer) widgetContainer);
                }
            }
        });
        eventBus.addHandler(HeaderSelectionEvent.TYPE, new HeaderSelectionEvent.Handler() {
            @Override
            public void handle(HeaderSelectionEvent event) {
                show(event.getSelectedItem());
            }
        });
        reset();
    }

    public PropertiesPanel getView() {
        return view;
    }

    private void reset() {
        if (currentDesignWidget != null) {
            view.getPanel().remove(currentDesignWidget);
            currentDesignWidget = null;
        }

        view.getRequiredGroup().setVisible(false);
        view.getReadOnlyGroup().setVisible(false);
        view.getSkipGroup().setVisible(false);

        if (labelKeyUpHandler != null) {
            labelKeyUpHandler.removeHandler();
        }
        if (descriptionKeyUpHandler != null) {
            descriptionKeyUpHandler.removeHandler();
        }
        if (requiredValueChangeHandler != null) {
            requiredValueChangeHandler.removeHandler();
        }
        if (readonlyValueChangeHandler != null) {
            readonlyValueChangeHandler.removeHandler();
        }
        if (skipButtonClickHandler != null) {
            skipButtonClickHandler.removeHandler();
        }
    }

    private void show(final FieldWidgetContainer fieldWidgetContainer) {
        reset();

        final FormField formField = fieldWidgetContainer.getFormField();

        view.setVisible(true);
        view.getRequiredGroup().setVisible(true);
        view.getReadOnlyGroup().setVisible(true);
        view.getSkipGroup().setVisible(true);

        view.getLabel().setValue(Strings.nullToEmpty(formField.getLabel()));
        view.getDescription().setValue(Strings.nullToEmpty(formField.getDescription()));
        view.getRequired().setValue(formField.isRequired());

        setSkipState(formField);
        skipButtonClickHandler = view.getSkipButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                SkipDialog dialog = new SkipDialog(fieldWidgetContainer, PropertiesPresenter.this);
                dialog.show();
            }
        });

        labelKeyUpHandler = view.getLabel().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                formField.setLabel(view.getLabel().getValue());
                fieldWidgetContainer.syncWithModel();
            }
        });
        descriptionKeyUpHandler = view.getDescription().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                formField.setDescription(view.getDescription().getValue());
                fieldWidgetContainer.syncWithModel();
            }
        });
        requiredValueChangeHandler = view.getRequired().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                formField.setRequired(view.getRequired().getValue());
                fieldWidgetContainer.syncWithModel();
            }
        });
        readonlyValueChangeHandler = view.getReadOnly().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                formField.setReadOnly(view.getReadOnly().getValue());
                fieldWidgetContainer.syncWithModel();
            }
        });

        ResourceLocator locator = fieldWidgetContainer.getFormDesigner().getResourceLocator();
        currentDesignWidget = new SimpleFormPanel(locator,
                new VerticalFieldContainer.Factory(),
                new FormFieldWidgetFactory(locator), false);
        if(formField.getType() instanceof ParametrizedFieldType) {
            ParametrizedFieldType parametrizedType = (ParametrizedFieldType) formField.getType();
            currentDesignWidget.asWidget().setVisible(true);
            currentDesignWidget.show(Resources.createResource(parametrizedType.getParameters()));

        } else {
            currentDesignWidget.asWidget().setVisible(false);
        }
        view.getPanel().add(currentDesignWidget);
    }

    public void setSkipState(FormField formField) {
        view.getSkipState().setText(formField.hasSkipExpression() ? I18N.CONSTANTS.defined() : I18N.CONSTANTS.no());
        view.getSkipExpression().setInnerText(formField.getSkipExpression());

        if (formField.hasSkipExpression()) {
            view.getSkipExpression().removeClassName("hide");
        } else if (!view.getSkipExpression().getClassName().contains("hide")) {
            view.getSkipExpression().addClassName("hide");
        }
    }

    public void show(final HeaderPresenter headerPresenter) {
        reset();

        final FormClass formClass = headerPresenter.getFormClass();

        view.setVisible(true);
        view.getLabel().setValue(Strings.nullToEmpty(formClass.getLabel()));
        view.getDescription().setValue(Strings.nullToEmpty(formClass.getDescription()));
        labelKeyUpHandler = view.getLabel().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                formClass.setLabel(view.getLabel().getValue());
                headerPresenter.show();
            }
        });

        descriptionKeyUpHandler = view.getDescription().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                formClass.setDescription(view.getDescription().getValue());
                headerPresenter.show();
            }
        });
    }
}
