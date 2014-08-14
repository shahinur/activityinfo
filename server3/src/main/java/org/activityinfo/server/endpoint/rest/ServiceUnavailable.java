package org.activityinfo.server.endpoint.rest;

import javax.ws.rs.core.Response;

public class ServiceUnavailable {


    static Response serviceUnavailable() {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity("The ActivityInfo 2.0 API is no longer available.")
                .build();
    }
}
