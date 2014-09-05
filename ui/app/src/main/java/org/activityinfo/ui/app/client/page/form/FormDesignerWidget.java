package org.activityinfo.ui.app.client.page.form;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.component.FormLoader;
import org.activityinfo.ui.vdom.shared.tree.VWidget;

class FormDesignerWidget extends VWidget {

    private final FormPage page;

    public FormDesignerWidget(FormPage page) {
        this.page = page;
    }

    @Override
    public Widget createWidget() {
        final FlowPanel flowPanel = new FlowPanel();
        flowPanel.add(new Label("Loading..."));
        FormLoader.loadDesigner(page.getFormClass()).then(new AsyncCallback<Widget>() {
            @Override
            public void onFailure(Throwable caught) {
                flowPanel.clear();
                flowPanel.add(new Label("Error loading form designer"));
            }

            @Override
            public void onSuccess(Widget result) {
                flowPanel.clear();
                flowPanel.add(result);
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

        FormDesignerWidget that = (FormDesignerWidget) o;

        return this.page.getResourceId().equals(that.page.getResourceId());
    }

    @Override
    public int hashCode() {
        return page != null ? page.hashCode() : 0;
    }

}
