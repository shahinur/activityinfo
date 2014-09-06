package org.activityinfo.store.cloudsql;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class BadRequestException extends WebApplicationException {

    private String message;

    public BadRequestException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST).entity(message).build());
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
