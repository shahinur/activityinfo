package org.activityinfo.ui.client.pageView.formClass;

import com.google.common.base.Function;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.component.formdesigner.FormDesignerPanel;
import org.activityinfo.ui.client.pageView.InstancePageView;
import org.activityinfo.ui.client.widget.DisplayWidget;

import javax.annotation.Nullable;

/**
 * This is page view for designing a FormClass. It is shown for the /design url
 *
 * Created by Mithun on 4/3/2014.
 */
public class DesignTab implements DisplayWidget<FormInstance> {

    private ResourceLocator resourceLocator;
    private FlowPanel panel;

    public DesignTab(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        this.panel = new FlowPanel();
    }

    @Override
    public Promise<Void> show(FormInstance value) {
        return this.resourceLocator.getFormClass(value.getId())
                .then(new Function<FormClass, Void>() {
                    @Nullable
                    @Override
                    public Void apply(FormClass formClass) {
                        panel.add(new FormDesignerPanel(resourceLocator, formClass));
                        return null;
                    }
                });
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
