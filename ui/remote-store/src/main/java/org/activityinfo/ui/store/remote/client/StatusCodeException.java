package org.activityinfo.ui.store.remote.client;

public class StatusCodeException extends RuntimeException {
    private int statusCode;

    public StatusCodeException() {
    }

    public StatusCodeException(int statusCode) {
        super(Integer.toString(statusCode));
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
