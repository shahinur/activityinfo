package org.activityinfo.store.cloudsql;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class BadRequestException extends WebApplicationException {

    public BadRequestException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST).entity(message).build());
    }
}
