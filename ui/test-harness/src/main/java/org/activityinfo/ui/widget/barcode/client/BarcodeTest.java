package org.activityinfo.ui.widget.barcode.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BarcodeTest implements EntryPoint {

    private static final Logger LOGGER = Logger.getLogger(BarcodeTest.class.getName());

    @Override
    public void onModuleLoad() {

        Button button = new Button(SafeHtmlUtils.fromString("Scan barcode"));
        final Label result = new Label();

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.add(button);
        flowPanel.add(result);

        RootPanel.get().add(flowPanel);

        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                BarcodeScanner.get().scan().then(new AsyncCallback<ScanResult>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        result.setText("Failure: " + throwable.getMessage());
                        LOGGER.log(Level.SEVERE, "Bar code scanning failure", throwable);
                    }

                    @Override
                    public void onSuccess(ScanResult scanResult) {
                        if(scanResult.getOutcome() == ScanOutcome.SUCCEEDED) {
                            result.setText(scanResult.getMessage());
                        } else {
                            result.setText(scanResult.getOutcome().name());
                        }
                    }
                });
            }
        });

    }
}
