package org.activityinfo.ui.vdom.shared.html;

import com.google.gwt.core.client.JavaScriptObject;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;

public final class Icon extends JavaScriptObject {

    protected Icon() {}

    public static native Icon valueOf(String classNames) /*-{
        return classNames;
    }-*/;

    public final VNode render() {
        return new VNode(HtmlTag.SPAN, PropMap.withClasses(getClassNames()));
    }

    public native String getClassNames() /*-{
        return this;
    }-*/;

    public CssClass getCssClass() {
        return CssClass.valueOf(getClassNames());
    }
}
