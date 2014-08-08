package org.activityinfo.ui.widget.barcode.client;

/**
 * The result of a barcode scan.
 */
public class ScanResult {
    private String encoding;
    private String message;
    private ScanOutcome outcome;

    ScanResult(String encoding, String message) {
        this.encoding = encoding;
        this.message = message;
        this.outcome = ScanOutcome.SUCCEEDED;
    }

    ScanResult(ScanOutcome outcome) {
        this.outcome = outcome;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getMessage() {
        return message;
    }

    public ScanOutcome getOutcome() {
        return outcome;
    }
}
