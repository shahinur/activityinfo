package org.activityinfo.ui.store.remote.client.table;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import org.activityinfo.model.table.ColumnType;

public final class ColumnViewOverlay extends JavaScriptObject {

    private static final DateTimeFormat DATE_FORMAT =
        DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.ISO_8601);

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
