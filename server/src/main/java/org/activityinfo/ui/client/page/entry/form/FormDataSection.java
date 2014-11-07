package org.activityinfo.ui.client.page.entry.form;
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

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.legacy.shared.model.IsFormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.component.form.SimpleFormPanel;
import org.activityinfo.ui.client.component.form.VerticalFieldContainer;
import org.activityinfo.ui.client.component.form.field.FieldWidgetMode;
import org.activityinfo.ui.client.component.form.field.FormFieldWidgetFactory;
import org.activityinfo.ui.client.widget.LoadingPanel;
import org.activityinfo.ui.client.widget.loading.PageLoadingPanel;

import javax.inject.Provider;

/**
 * @author yuriyz on 11/07/2014.
 */
public class FormDataSection implements FormSection {

    private final ResourceLocator resourceLocator;
    private final LayoutContainer asGxtComponent;
    private final SimpleFormPanel formPanel;

    private final LoadingPanel<FormInstance> loadingPanel;

    public FormDataSection(final Dispatcher dispatcher, final IsFormClass formClass) {
        this.resourceLocator = new ResourceLocatorAdaptor(dispatcher);
        this.formPanel = new SimpleFormPanel(
                resourceLocator,
                new VerticalFieldContainer.Factory(),
                new FormFieldWidgetFactory(resourceLocator, FieldWidgetMode.NORMAL));

        loadingPanel = new LoadingPanel<FormInstance>(new PageLoadingPanel()) {
            @Override
            protected void setWidget(IsWidget widget) {
                super.setWidget(widget);
                if (asGxtComponent != null) {
                    asGxtComponent.layout(true);
                }
            }
        };
        loadingPanel.setDisplayWidget(formPanel);
        loadingPanel.show(new Provider<Promise<FormInstance>>() {

            @Override
            public Promise<FormInstance> get() {
                return resourceLocator.getFormInstance(formClass.getResourceId());
            }
        });

        asGxtComponent = new LayoutContainer(new FitLayout());
        asGxtComponent.add(loadingPanel.asWidget());

    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public void updateModel(Object m) {

    }

    @Override
    public void updateForm(Object m, boolean isNew) {

    }

    @Override
    public Component asComponent() {
        return asGxtComponent;
    }

    public void save() {
        resourceLocator.persist(formPanel.getInstance()).then(new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Save failed", caught);
                // todo
            }

            @Override
            public void onSuccess(Void result) {
                // todo
            }
        });
    }
}
