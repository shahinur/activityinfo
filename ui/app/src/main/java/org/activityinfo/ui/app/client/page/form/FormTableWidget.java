package org.activityinfo.ui.app.client.page.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import org.activityinfo.service.store.ResourceLocator;
import org.activityinfo.service.store.ResourceLocatorAdaptor;
import org.activityinfo.ui.app.client.AppEntryPoint;
import org.activityinfo.ui.component.table.TablePage;
import org.activityinfo.ui.vdom.shared.tree.VWidget;

class FormTableWidget extends VWidget {

    private FormPage formPage;

    FormTableWidget(FormPage formPage) {
        this.formPage = formPage;
    }

    @Override
    public IsWidget createWidget() {
        final FlowPanel flowPanel = new FlowPanel();
        flowPanel.add(new Label("Loading..."));
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
                flowPanel.clear();
                flowPanel.add(new Label("Failed to load table component: " + reason.getMessage()));
            }

            @Override
            public void onSuccess() {
                ResourceLocator adapter = new ResourceLocatorAdaptor(AppEntryPoint.service);
                TablePage tablePage = new TablePage(adapter);
                tablePage.show(formPage.getResourceId());
                flowPanel.add(tablePage);
            }
        });
        return flowPanel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FormTableWidget that = (FormTableWidget) o;
        return this.formPage.getResourceId().equals(that.formPage.getResourceId());
    }

    @Override
    public int hashCode() {
        return formPage != null ? formPage.hashCode() : 0;
    }
}
