package org.activityinfo.ui.widget.barcode.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;


public interface DecoderClientBundle extends ClientBundle {

    public static final DecoderClientBundle INSTANCE = GWT.create(DecoderClientBundle.class);

    /**
     * The Decoder worker script is minified by Maven during the generate-resources phase.
     */
    @Source("worker.min.js")
    @DataResource.DoNotEmbed
    @DataResource.MimeType("application/javascript")
    DataResource getWorkerScript();

}
