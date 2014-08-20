package org.activityinfo.ui.app.client.page.form;

import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.service.store.ResourceLocator;
import org.activityinfo.service.store.ResourceLocatorAdaptor;
import org.activityinfo.ui.app.client.AppEntryPoint;
import org.activityinfo.ui.component.formdesigner.FormDesigner;
import org.activityinfo.ui.component.formdesigner.FormDesignerPanel;
import org.activityinfo.ui.vdom.shared.tree.Destructible;
import org.activityinfo.ui.vdom.shared.tree.VWidget;

class FormDesignerWidget extends VWidget implements Destructible {

    private final FormPage page;

    public FormDesignerWidget(FormPage page) {
        this.page = page;
    }

    @Override
    public Widget createWidget() {
        ResourceLocator adapter = new ResourceLocatorAdaptor(AppEntryPoint.service);
        FormClass formClass = page.getFormClass();
        FormDesignerPanel formDesignerPanel = new FormDesignerPanel(adapter, formClass);
        FormDesigner designer = new FormDesigner(formDesignerPanel, adapter, formClass);
        return formDesignerPanel.asWidget();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FormDesignerWidget that = (FormDesignerWidget) o;

        return this.page.getResourceId().equals(that.page.getResourceId());
    }

    @Override
    public int hashCode() {
        return page != null ? page.hashCode() : 0;
    }

    @Override
    public void destroy(Object node) {

    }
}
