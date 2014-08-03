package org.activityinfo.ui.client.service.table;

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
