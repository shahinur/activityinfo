package org.activityinfo.ui.vdom.shared.html;

import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;

public final class Icon {
    private String classNames;

    public static Icon valueOf(String classNames) {
        return new Icon(classNames);
    }

    private Icon(String classNames) {
        this.classNames = classNames;
    }

    public final VNode render() {
        return new VNode(HtmlTag.SPAN, PropMap.withClasses(classNames));
    }

    public String getClassNames() {
        return classNames;
    }

    public CssClass getCssClass() {
        return CssClass.valueOf(classNames);
    }
}
