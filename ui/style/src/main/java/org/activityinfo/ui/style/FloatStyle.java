package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.CssClass;

public enum FloatStyle {
    NONE {
        @Override
        public CssClass className() {
            return CssClass.valueOf(" ");
        }
    },
    LEFT {
        @Override
        public CssClass className() {
            return BaseStyles.PULL_LEFT;
        }
    },
    RIGHT {
        @Override
        public CssClass className() {
            return BaseStyles.PULL_RIGHT;
        }
    };

    public abstract CssClass className();
}
