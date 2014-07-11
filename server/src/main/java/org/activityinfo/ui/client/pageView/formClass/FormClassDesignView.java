package org.activityinfo.ui.client.pageView.formClass;

import com.google.common.base.Function;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.component.formdesigner.FormDesignerPanel;
import org.activityinfo.ui.client.pageView.InstancePageView;

import javax.annotation.Nullable;

/**
 * This is page view for designing a FormClass. It is shown for the /design url
 *
 * Created by Mithun on 4/3/2014.
 */
public class FormClassDesignView implements InstancePageView{

    interface FormClassDesignViewUiBinder extends UiBinder<HTMLPanel, FormClassDesignView> {
    }

    private static FormClassDesignViewUiBinder ourUiBinder = GWT.create(FormClassDesignViewUiBinder.class);

    private ResourceLocator resourceLocator;
    private final HTMLPanel rootElement;

    public FormClassDesignView(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        rootElement = ourUiBinder.createAndBindUi(this);
    }

    @Override
    public Promise<Void> show(FormInstance value) {
        return this.resourceLocator.getFormClass(value.getId())
                .then(new Function<FormClass, Void>() {
                    @Nullable
                    @Override
                    public Void apply(FormClass formClass) {
                        rootElement.add(new FormDesignerPanel(resourceLocator, formClass));
                        return null;
                    }
                });
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }
}
