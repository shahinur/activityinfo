package org.activityinfo.ui.client.chrome;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel implements IsWidget {

    private final HTMLPanel rootElement;

    interface MainPanelUiBinder extends UiBinder<HTMLPanel, MainPanel> {
    }

    private static MainPanelUiBinder ourUiBinder = GWT.create(MainPanelUiBinder.class);

    @UiField PageHeader pageHeader;
    @UiField HeaderBar headerBar;

    public MainPanel() {
        rootElement = ourUiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    public PageHeader getPageHeader() {
        return pageHeader;
    }

    public HeaderBar getHeaderBar() {
        return headerBar;
    }
}