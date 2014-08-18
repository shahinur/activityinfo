package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.HasClassNames;

public enum ButtonSize implements HasClassNames {
    LG,
    SM,
    XS;

    @Override
    public String getClassNames() {
        return "btn-" + name().toLowerCase();
    }
}
