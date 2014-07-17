package org.activityinfo.ui.client.widget.loading;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Widget that displays load status
 */
public interface LoadingView {

    void onLoadingStateChanged(LoadingState state, Throwable caught);

    HasClickHandlers getRetryButton();

}
