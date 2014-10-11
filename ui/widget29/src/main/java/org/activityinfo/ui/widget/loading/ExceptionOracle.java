package org.activityinfo.ui.widget.loading;

import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.style.icons.FontAwesome;

/**
 * Provides user-intelligible information about exceptions
 */
public class ExceptionOracle {

    public static void setLoadingStyle(Widget widget, LoadingState state) {
        widget.setStyleName(LoadingStylesheet.INSTANCE.loading(), state == LoadingState.LOADING);
        widget.setStyleName(LoadingStylesheet.INSTANCE.failed(), state == LoadingState.FAILED);
        widget.setStyleName(LoadingStylesheet.INSTANCE.loaded(), state == LoadingState.LOADED);
    }

    private static boolean isConnectionFailure(Throwable caught) {
        return false;
    }

    public static String getIcon(Throwable caught) {
        return isConnectionFailure(caught) ?
                FontAwesome.BOLT.getClassNames() :
                FontAwesome.BUG.getClassNames();
    }

    public static String getHeading(Throwable caught) {
        return isConnectionFailure(caught) ?
                I18N.CONSTANTS.connectionProblem()   :
                I18N.CONSTANTS.unexpectedException() ;
    }

    public static String getExplanation(Throwable caught) {
        return isConnectionFailure(caught) ?
            I18N.CONSTANTS.connectionProblemText() :
            I18N.CONSTANTS.unexpectedExceptionExplanation();
    }
}
