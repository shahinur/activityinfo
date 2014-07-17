package org.activityinfo.ui.client.chrome;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.OListElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class PageHeader implements IsWidget {

    private final HTMLPanel rootElement;

    interface PageHeaderUiBinder extends UiBinder<HTMLPanel, PageHeader> {
    }

    private static PageHeaderUiBinder ourUiBinder = GWT.create(PageHeaderUiBinder.class);


    @UiField SpanElement pageTitleSpan;
    @UiField SpanElement subTitleSpan;
    @UiField OListElement breadCrumbList;
    @UiField Element iconSpan;

    public PageHeader() {
        rootElement = ourUiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    public void setIconStyle(String classNames) {
        iconSpan.setClassName(classNames);
    }

    public void setPageTitle(String pageTitle) {
        pageTitleSpan.setInnerText(pageTitle);
    }
}