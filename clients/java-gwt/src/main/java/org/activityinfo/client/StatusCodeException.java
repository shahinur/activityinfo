package org.activityinfo.client;

/**
 * Thrown upon receiving an http response with a status code
 * outside the 200-299 range.
 */
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
