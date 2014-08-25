package org.activityinfo.ui.store.remote.client.resource;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public final class ResourceNodeOverlay extends JavaScriptObject {
    protected ResourceNodeOverlay() {
    }

    public native String getString(String propertyName) /*-{
        return this[propertyName];
    }-*/;

    public native JsArray<ResourceNodeOverlay> getChildren() /*-{
        return this.children;
    }-*/;


    // TODO: actually parse longs... need to transfer as string?
    public native int getLong(String propertyName) /*-{
        return this[propertyName];
    }-*/;
}
