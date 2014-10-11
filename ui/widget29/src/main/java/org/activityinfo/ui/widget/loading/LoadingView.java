package org.activityinfo.ui.widget.loading;

import com.google.gwt.event.dom.client.HasClickHandlers;

/**
 * Widget that displays load status
 */
public interface LoadingView {

    void onLoadingStateChanged(LoadingState state, Throwable caught);

    HasClickHandlers getRetryButton();

}
