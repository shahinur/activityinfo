package org.activityinfo.ui.client.widget.loading;

import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.style.Icons;

/**
 * Provides user-intelligible information about exceptions
 */
public class ExceptionOracle {

    public static void setLoadingStyle(Widget widget, LoadingState state) {
        widget.setStyleName(LoadingStylesheet.INSTANCE.loading(), state == LoadingState.LOADING);
        widget.setStyleName(LoadingStylesheet.INSTANCE.failed(), state == LoadingState.FAILED);
        widget.setStyleName(LoadingStylesheet.INSTANCE.loaded(), state == LoadingState.LOADED);
    }
//
//    public static void displayException(Throwable caught) {
//        if(isConnectionFailure(caught)) {
//            view.showFailure(
//                    Icons.INSTANCE.connectionProblem(),
//                    I18N.CONSTANTS.connectionProblem(),
//                    I18N.CONSTANTS.connectionProblemText());
//        } else {
//            view.showFailure(
//                    Icons.INSTANCE.connectionProblem(),
//                    I18N.CONSTANTS.connectionProblem(),
//                    I18N.CONSTANTS.connectionProblemText());
//        }
//    }

    private static boolean isConnectionFailure(Throwable caught) {
        return false;
    }

    public static String getIcon(Throwable caught) {
        return isConnectionFailure(caught) ? Icons.INSTANCE.connectionProblem() :
                Icons.INSTANCE.connectionProblem();
    }

    public static String getHeading(Throwable caught) {
        return I18N.CONSTANTS.connectionProblem();
    }

    public static String getExplanation(Throwable caught) {
        return I18N.CONSTANTS.connectionProblemText();
    }
}
