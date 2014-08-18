package org.activityinfo.ui.client.chrome;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class HeaderBar implements IsWidget {

    private final HTMLPanel rootElement;

    interface HeaderPanelUiBinder extends UiBinder<HTMLPanel, HeaderBar> {
    }

    private static HeaderPanelUiBinder ourUiBinder = GWT.create(HeaderPanelUiBinder.class);


    @UiField SpanElement userNameSpan;

    public HeaderBar() {
        rootElement = ourUiBinder.createAndBindUi(this);
        userNameSpan.setInnerText("Your name here");
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }
}