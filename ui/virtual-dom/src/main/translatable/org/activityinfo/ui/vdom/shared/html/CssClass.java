package org.activityinfo.ui.vdom.shared.html;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

import java.lang.IllegalStateException;

/**
 * Ensures that the CssClass instances are compiled to JavaScript
 * as simple strings to reduce compilation size.
 */
public final class CssClass extends JavaScriptObject {

    protected CssClass() { }

    public static CssClass valueOf(String classNames) {
        if(!GWT.isScript()) {
            throw new IllegalStateException(
                "VDomOverlays.gwt.xml should not be included while running dev mode. " +
                "Make sure you are only inheriting VDom.gwt.xml while running in DevMode. ");
        }
        return _valueOf(classNames);
    }

    public static native CssClass _valueOf(String classNames) /*-{
        return classNames;
    }-*/;

    public native String getClassNames() /*-{
        return this;
    }-*/;

}
