package org.activityinfo.ui.widget.barcode.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.CanvasElement;

/**
 * Interface to the decoding javascript worker that actually
 * processes the barcodes.
 */
public final class Decoder extends JavaScriptObject {
    protected Decoder() {
    }

    public static Decoder create() {
        return create(DecoderClientBundle.INSTANCE.getWorkerScript().getSafeUri().asString());
    }

    private static native Decoder create(String workerScriptUri) /*-{
      return new Worker(workerScriptUri);
    }-*/;

    public native void setCallback(DecoderCallback callback) /*-{
      this.onmessage = function(event) {
        callback.@org.activityinfo.ui.widget.barcode.client.DecoderCallback::onMessage(Lorg/activityinfo/ui/widget/barcode/client/DecoderEvent;)(event);
      };
      this.onerror = function(event) {
        callback.@org.activityinfo.ui.widget.barcode.client.DecoderCallback::onError(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
      }
    }-*/;


    public void decode(Canvas canvas, Context2d ctx, String command) {
        doDecode(command, canvas.getCanvasElement(), ctx,
                canvas.getCoordinateSpaceWidth(),
                canvas.getCoordinateSpaceHeight());
    }

    /**
     * Constructs a command message for the worker
     * @param canvas
     * @param ctx
     * @return
     */
    private native void doDecode(String command, CanvasElement canvas, Context2d ctx, int sw, int sh) /*-{
      var imageData = ctx.getImageData(0,0,sw,sh).data;
      var message = {
        ImageData: imageData,
        Width: canvas.width,
        Height: canvas.height,
        cmd: command };
      this.postMessage(message);
    }-*/;
}
