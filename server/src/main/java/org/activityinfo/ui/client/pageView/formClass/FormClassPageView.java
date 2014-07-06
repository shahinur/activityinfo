package org.activityinfo.ui.client.pageView.formClass;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.page.instance.BreadCrumbBuilder;
import org.activityinfo.ui.client.pageView.IconStyleProvider;
import org.activityinfo.ui.client.pageView.InstancePageView;
import org.activityinfo.ui.client.style.Icons;
import org.activityinfo.ui.client.widget.LoadingPanel;

/**
 * Provides a view for a FormClass instance
 */
public class FormClassPageView implements InstancePageView {

    private final Widget rootElement;
    private final ResourceLocator resourceLocator;

    interface FormViewUiBinder extends UiBinder<HTMLPanel, FormClassPageView> {
    }

    private static FormViewUiBinder ourUiBinder = GWT.create(FormViewUiBinder.class);

    private BreadCrumbBuilder breadCrumb;
    private ScrollPanel scrollAncestor;

    @UiField
    Element nameElement;

    @UiField
    Element pageIcon;

    @UiField
    Element breadCrumbElement;

    @UiField
    LoadingPanel<FormTree> contentPanel;

    public FormClassPageView(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        rootElement = ourUiBinder.createAndBindUi(this);
        breadCrumb = new BreadCrumbBuilder(resourceLocator, breadCrumbElement);

        Icons.INSTANCE.ensureInjected();
    }

    public Promise<Void> show(FormInstance instance) {
        ResourceId classId = instance.getId();

        nameElement.setInnerText(instance.getString(FormClass.LABEL_FIELD_ID));
        pageIcon.setClassName(IconStyleProvider.getIconStyleForFormClass(instance.getId()));

        breadCrumb.show(instance);

        final TablePresenter tablePresenter = new TablePresenter(resourceLocator);
        tablePresenter.setScrollAncestor(scrollAncestor);
        contentPanel.setDisplayWidget(tablePresenter);
        return contentPanel.show(new AsyncFormTreeBuilder(resourceLocator), classId);
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    public ScrollPanel getScrollAncestor() {
        return scrollAncestor;
    }

    public void setScrollAncestor(ScrollPanel scrollAncestor) {
        this.scrollAncestor = scrollAncestor;
    }
}