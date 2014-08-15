package org.activityinfo.ui.store.remote.client.table;

import com.google.gwt.core.client.JavaScriptObject;

public final class JsStringColumnArray extends JavaScriptObject {

    protected JsStringColumnArray() {

    }

    public native String getString(int rowIndex) /*-{
      return this[rowIndex];
    }-*/;

    public native double getNumber(int rowIndex) /*-{
      return parseFloat(this[rowIndex]);
    }-*/;

}
