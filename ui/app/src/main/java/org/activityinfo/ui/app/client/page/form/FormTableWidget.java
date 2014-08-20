package org.activityinfo.ui.app.client.page.form;

import com.google.gwt.user.client.ui.IsWidget;
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
        ResourceLocator adapter = new ResourceLocatorAdaptor(AppEntryPoint.service);
        TablePage tablePage = new TablePage(adapter);
        tablePage.show(formPage.getResourceId());
        return tablePage.asWidget();
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
