package org.activityinfo.ui.store.remote.client.resource;

import com.google.gwt.core.client.JavaScriptObject;

public final class ResourceTreeOverlay extends JavaScriptObject {
    protected ResourceTreeOverlay() {
    }

    public native ResourceNodeOverlay getRootNode() /*-{
      return this.rootNode;
    }-*/;
}
