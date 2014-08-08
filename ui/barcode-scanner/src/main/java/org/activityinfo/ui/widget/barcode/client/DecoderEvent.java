package org.activityinfo.ui.widget.barcode.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Event emitted by the decoding worker
 */
public final class DecoderEvent extends JavaScriptObject {
    protected DecoderEvent() {
    }

    public native boolean isLoggingMessage() /*-{
      return this.data.success === "log";
    }-*/;

    public native String getLogMessage() /*-{
      return this.data.result;
    }-*/;

    public native boolean isFinished() /*-{
      return this.data.finished;
    }-*/;

    public native boolean isSuccess() /*-{
      return this.data.success;
    }-*/;

    private native JsArrayString getResultArray() /*-{
      return this.data.result;
    }-*/;

    public ScanResult getResult() {
        JsArrayString results = getResultArray();
        String result = results.get(0);
        int colon = result.indexOf(':');
        String encoding = result.substring(0, colon).trim();
        String content = result.substring(colon+1).trim();
        return new ScanResult(encoding, content);
    }



}
