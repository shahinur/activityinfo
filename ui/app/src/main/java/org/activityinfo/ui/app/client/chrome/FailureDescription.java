package org.activityinfo.ui.app.client.chrome;

import com.google.gwt.http.client.RequestTimeoutException;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;

public class FailureDescription {

    private Icon icon;
    private String message;
    private String description;

    public static FailureDescription of(Throwable e) {
        if(e instanceof RequestTimeoutException) {
            FailureDescription d = new FailureDescription();
            d.icon = FontAwesome.PAPER_PLANE_O;
            d.message = "Check your internet connection.";
            d.description = "We weren't able to reach the server, double check that you're " +
                            "online and try again.";
            return d;
        } else {

            FailureDescription d = new FailureDescription();
            d.icon = FontAwesome.BUG;
            d.message = "Oh no! Something went wrong.";
            d.description = "We're very sorry but something isn't quite working correctly. " +
                            "We'll try to fix it asap!";
            return d;
        }
    }

    public Icon getIcon() {
        return icon;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
