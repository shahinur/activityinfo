package org.activityinfo.ui.client.widget.loading;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.client.widget.Button;
import org.activityinfo.ui.client.widget.ButtonWithSize;

/**
 * Displays the loading state of a table
 */
public class TableLoadingIndicator implements IsWidget, LoadingView {

    private final HTMLPanel rootElement;

    @UiField Button retryButton;
    @UiField SpanElement messageSpan;

    interface LoadingIndicatorUiBinder extends UiBinder<HTMLPanel, TableLoadingIndicator> {
    }

    private static LoadingIndicatorUiBinder ourUiBinder = GWT.create(LoadingIndicatorUiBinder.class);

    public TableLoadingIndicator() {
        rootElement = ourUiBinder.createAndBindUi(this);
    }

    @Override
    public void onLoadingStateChanged(final LoadingState state, final Throwable caught) {
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                ExceptionOracle.setLoadingStyle(rootElement, state);
                messageSpan.setInnerText(ExceptionOracle.getHeading(caught));
                return false;
            }
        }, 500);

    }

    @Override
    public HasClickHandlers getRetryButton() {
        return retryButton;
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }
}