package org.activityinfo.ui.style;

import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;

public class Spinners {

    public static final Icon spinner() {
        return Icon.valueOf(FontAwesome.SPINNER + " fa-spin");
    }
}
