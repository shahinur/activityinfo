package org.activityinfo.ui.store.remote.client.table;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public final class TableDataOverlay extends JavaScriptObject {

    protected TableDataOverlay() {
    }

    public native int getNumRows() /*-{
      return this.rows;
    }-*/;

    public native JsArrayString getColumns() /*-{
      var keys = [];
      for (var key in this.columns) {
        if (this.columns.hasOwnProperty(key)) {
          keys.push(key);
        }
      }
      return keys;
    }-*/;

    public native ColumnViewOverlay getColumn(String columnId) /*-{
      return this.columns[columnId];
    }-*/;

}
