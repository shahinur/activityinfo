package org.activityinfo.ui.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.ResourceLocator;
import org.activityinfo.service.store.ResourceLocatorAdaptor;
import org.activityinfo.ui.app.client.AppEntryPoint;
import org.activityinfo.ui.component.formdesigner.FormDesigner;
import org.activityinfo.ui.component.formdesigner.FormDesignerPanel;

public class FormLoader {

    public static Promise<Widget> loadDesigner(final FormClass formClass) {
        final Promise<Widget> widget = new Promise<>();
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
                widget.reject(reason);
            }

            @Override
            public void onSuccess() {
                Widget panel;
                try {
                    ResourceLocator adapter = new ResourceLocatorAdaptor(AppEntryPoint.service);
                    FormDesignerPanel formDesignerPanel = new FormDesignerPanel(adapter, formClass);
                    FormDesigner designer = new FormDesigner(formDesignerPanel, adapter, formClass);
                    panel = formDesignerPanel.asWidget();
                } catch(Throwable caught) {
                    widget.reject(caught);
                    return;
                }
                widget.resolve(panel);
            }
        });
        return widget;
    }

}
