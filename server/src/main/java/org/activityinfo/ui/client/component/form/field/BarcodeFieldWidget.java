package org.activityinfo.ui.client.component.form.field;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.barcode.BarcodeValue;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.widget.ButtonWithIcon;
import org.activityinfo.ui.client.widget.TextBox;
import org.activityinfo.ui.widget.barcode.client.BarcodeScanner;
import org.activityinfo.ui.widget.barcode.client.ScanOutcome;
import org.activityinfo.ui.widget.barcode.client.ScanResult;

public class BarcodeFieldWidget implements FormFieldWidget<BarcodeValue> {


    interface BarcodeFieldWidgetUiBinder extends UiBinder<HTMLPanel, BarcodeFieldWidget> {
    }

    private static BarcodeFieldWidgetUiBinder ourUiBinder = GWT.create(BarcodeFieldWidgetUiBinder.class);

    private static final String PLACEHOLDER_TEXT = "ABCDEF0123456";

    private final HTMLPanel panel;

    @UiField TextBox textBox;
    @UiField ButtonWithIcon scanButton;
    @UiField InlineLabel statusLabel;

    public BarcodeFieldWidget(final ValueUpdater<TextValue> valueUpdater) {
        panel = ourUiBinder.createAndBindUi(this);

        this.textBox.getElement().setAttribute("placeholder", PLACEHOLDER_TEXT);
        this.textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                valueUpdater.update(TextValue.valueOf(event.getValue()));
            }

        });
        this.textBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                valueUpdater.update(getValue());
            }
        });
    }

    private TextValue getValue() {
        return TextValue.valueOf(BarcodeFieldWidget.this.textBox.getValue());
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        textBox.setReadOnly(readOnly);
    }

    @Override
    public Promise<Void> setValue(BarcodeValue value) {
        textBox.setValue(value.getCode());
        return Promise.done();
    }

    @Override
    public void clearValue() {
        textBox.setValue(null);
    }

    @Override
    public void setType(FieldType type) {

    }

    @UiHandler("scanButton")
    public void onScanClicked(ClickEvent event) {
        scanButton.setEnabled(false);
        statusLabel.setText("Scanning...");
        BarcodeScanner.get().scan().then(new AsyncCallback<ScanResult>() {
            @Override
            public void onFailure(Throwable throwable) {
                scanButton.setEnabled(true);
                Window.alert("An unknown error encountered while trying to scan the barcode.");
            }

            @Override
            public void onSuccess(ScanResult scanResult) {
                scanButton.setEnabled(true);
                if(scanResult.getOutcome() == ScanOutcome.SUCCEEDED) {
                    textBox.setValue(scanResult.getMessage(), true);
                    statusLabel.setText(null);
                } else if(scanResult.getOutcome() == ScanOutcome.FAILED) {
                    statusLabel.setText("Couldn't decode the bar code.");
                } else {
                    statusLabel.setText("Your browser doesn't support barcode scanning.");
                }
            }
        });
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
