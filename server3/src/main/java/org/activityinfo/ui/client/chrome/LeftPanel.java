package org.activityinfo.ui.client.chrome;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.client.ClientContext;

public class LeftPanel implements IsWidget {

    private final HTMLPanel rootElement;

    interface LeftPanelUiBinder extends UiBinder<HTMLPanel, LeftPanel> {
    }

    private static LeftPanelUiBinder ourUiBinder = GWT.create(LeftPanelUiBinder.class);

    @UiField SpanElement logoSpan;

    public LeftPanel() {
        rootElement = ourUiBinder.createAndBindUi(this);
        logoSpan.setInnerText(ClientContext.getAppTitle());
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

}