package org.activityinfo.ui.store.remote.client.table;

import com.google.gwt.core.client.JavaScriptObject;
import org.activityinfo.model.table.ColumnType;

public final class ColumnViewOverlay extends JavaScriptObject {
    protected ColumnViewOverlay() {
    }

    public ColumnType getType() {
        return ColumnType.valueOf(getTypeName());
    }

    public native String getTypeName() /*-{
      return this.type;
    }-*/;

    public native String getStorageMode() /*-{
      return this.storage;
    }-*/;

    public native String getStringValue() /*-{
      return this.value;
    }-*/;

    public native double getDoubleValue() /*-{
      return this.value;
    }-*/;

    public native JavaScriptObject getArray() /*-{
      return this.values;
    }-*/;

}
