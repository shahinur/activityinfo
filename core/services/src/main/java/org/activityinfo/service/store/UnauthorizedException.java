package org.activityinfo.service.store;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Thrown to indicate that a user is not authorized to perform an operation
 * on a given resource.
 */
public class UnauthorizedException extends WebApplicationException {

    public UnauthorizedException() {
        super(Response.Status.UNAUTHORIZED);
    }
}
