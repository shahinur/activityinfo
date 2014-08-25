package org.activityinfo.load;

import javax.ws.rs.core.Response;

public class RequestError {

    private final int statusCode;
    private String reason;
    private final String message;

    public RequestError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getReasonPhrase() {
        return Response.Status.fromStatusCode(statusCode).getReasonPhrase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RequestError that = (RequestError) o;

        if (statusCode != that.statusCode) {
            return false;
        }
        if (message != null ? !message.equals(that.message) : that.message != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = statusCode;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
