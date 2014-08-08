package org.activityinfo.ui.widget.barcode.client;

import com.google.gwt.core.client.JavaScriptObject;

public interface DecoderCallback {

    public void onMessage(DecoderEvent event);

    public void onError(JavaScriptObject event);
}
