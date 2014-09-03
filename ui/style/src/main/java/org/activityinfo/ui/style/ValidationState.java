package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.CssClass;

public enum ValidationState {

    SUCCESS {
        @Override
        CssClass className() {
            return BaseStyles.HAS_SUCCESS;
        }
    },
    WARNING {
        @Override
        CssClass className() {
            return BaseStyles.HAS_WARNING;
        }
    },
    ERROR {
        @Override
        CssClass className() {
            return BaseStyles.HAS_ERROR;
        }
    };

    abstract CssClass className();
}
