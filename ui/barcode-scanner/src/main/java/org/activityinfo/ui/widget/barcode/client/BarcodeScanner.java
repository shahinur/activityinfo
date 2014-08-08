package org.activityinfo.ui.widget.barcode.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Image;
import org.activityinfo.promise.Promise;

import java.util.logging.Logger;

/**
 * Barcode scanner implemented using HTML5 Canvas, File, and Worker APIs.
 */
public class BarcodeScanner {

    private static final Logger LOGGER = Logger.getLogger(BarcodeScanner.class.getName());

    private static BarcodeScanner INSTANCE = null;

    private FileUpload fileUpload;
    private Image image;

    private Canvas canvas;
    private Context2d context2d;

    private Decoder worker;

    private int attemptsRemaining = 0;

    private Promise<ScanResult> currentRequest = null;

    private boolean fileSelected = false;

    private BarcodeScanner() {

        InputElement fileUploadElement = Document.get().createFileInputElement();
        fileUploadElement.setAccept("image/*;capture=camera");
        fileUploadElement.getStyle().setPosition(Style.Position.ABSOLUTE);
        fileUploadElement.getStyle().setVisibility(Style.Visibility.HIDDEN);
        Document.get().getBody().appendChild(fileUploadElement);

        fileUpload = FileUpload.wrap(fileUploadElement);
        fileUpload.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                onFileChanged(event);
            }
        });

        ImageElement imageElement = Document.get().createImageElement();
        imageElement.setWidth(320);
        imageElement.setHeight(240);
        imageElement.setSrc("about:blank");
        imageElement.getStyle().setPosition(Style.Position.ABSOLUTE);
        imageElement.getStyle().setVisibility(Style.Visibility.HIDDEN);
        Document.get().getBody().appendChild(imageElement);

        image = Image.wrap(imageElement);
        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                onImageLoaded();
            }
        });
    }

    public static BarcodeScanner get() {
        if(INSTANCE == null) {
            INSTANCE = new BarcodeScanner();
        }
        return INSTANCE;
    }

    public Promise<ScanResult> scan() {
        if(currentRequest != null && !currentRequest.isSettled() && fileSelected) {
            return Promise.rejected(new IllegalStateException("Scan already in progress"));
        }

        fileSelected = false;
        triggerUpload(fileUpload.getElement());

        currentRequest = new Promise<>();

        return currentRequest;
    }


    private void onFileChanged(ChangeEvent event) {

        fileSelected = true;

        try {
            loadImage(event.getNativeEvent(), image.getElement());
        } catch(Exception e) {
            currentRequest.reject(e);
        }
    }

    private static native void triggerUpload(Element element) /*-{
      element.click();
    }-*/;


    /**
     * Uses either URL.createObjectURL or the Files API to load the selected file
     * into the image element.
     */
    private native void loadImage(JavaScriptObject event, Element imageElement) /*-{
      var files = event.target.files;
      if (files && files.length > 0) {
        var file = files[0];
        try {
          var URL = $wnd.URL || $wnd.webkitURL;
          var imgURL = URL.createObjectURL(file);
          imageElement.src = imgURL;
          URL.revokeObjectURL(imgURL);
        }
        catch (e) {
          try {
            var fileReader = new FileReader();
            fileReader.onload = function (event) {
              imageElement.src = event.target.result;
            };
            fileReader.readAsDataURL(file);
          }
          catch (e) {
            this.@org.activityinfo.ui.widget.barcode.client.BarcodeScanner::onLoadImageFailure()();
          }
        }
      }
    }-*/;

    private void onLoadImageFailure() {
        currentRequest.resolve(new ScanResult(ScanOutcome.UNSUPPORTED));
    }

    /**
     * Once the image is loaded, we can draw to a canvas element and grab the raw image data
     */
    private void onImageLoaded() {

        LOGGER.info("Image load event received.");

        try {
            drawToCanvas();
            startDecoding();

        } catch(Exception e) {
            currentRequest.reject(e);
        }
    }

    private void drawToCanvas() {
        if(canvas == null) {
            canvas = Canvas.createIfSupported();
            canvas.setCoordinateSpaceWidth(640);
            canvas.setCoordinateSpaceHeight(480);

            context2d = canvas.getContext2d();
        }

        LOGGER.info("Drawing image to canvas...");

        ImageElement imageElement = image.getElement().cast();
        context2d.drawImage(imageElement, 0, 0, 640, 480);

        LOGGER.info("Image has been drawn.");
    }

    private void startDecoding() {
        attemptsRemaining = 2;
        postWorkerMessage("normal");
    }

    private void postWorkerMessage(String command) {

        LOGGER.info("Posting message [" + command + "] to decoder.");

        if(worker == null) {
            worker = Decoder.create();
            worker.setCallback(new DecoderCallback() {
                @Override
                public void onMessage(DecoderEvent event) {
                    onWorkerMessage(event);
                }

                @Override
                public void onError(JavaScriptObject event) {
                    LOGGER.severe("Received error message from logger");
                    currentRequest.reject(new RuntimeException());
                }
            });
        }
        worker.decode(canvas, context2d, command);
    }

    private void onWorkerMessage(DecoderEvent event) {

        LOGGER.fine("Received message from decoder.");

        if(event.isLoggingMessage()) {
            LOGGER.info(event.getLogMessage());

        } else if(event.isFinished()) {
            LOGGER.warning("Decoder finished without success");
            attemptsRemaining--;
            if (attemptsRemaining > 0) {
                if (!currentRequest.isSettled())
                    postWorkerMessage("flip");
            } else {
                currentRequest.resolve(new ScanResult(ScanOutcome.FAILED));
            }

        } else if(event.isSuccess()) {
            currentRequest.resolve(event.getResult());

        } else {
            LOGGER.warning("Unhandled message from worker.");
        }
    }
}