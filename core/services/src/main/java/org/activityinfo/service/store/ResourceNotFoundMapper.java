package org.activityinfo.service.store;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ResourceNotFoundMapper implements ExceptionMapper<ResourceNotFound> {
    @Override
    public Response toResponse(ResourceNotFound exception) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity("Could not find resource " + exception.getResourceId())
                .build();
    }
}
