package org.activityinfo.service.blob;

import com.google.common.io.ByteSource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Restful endpoint for a single user blob, which provides
 *
 */
public interface BlobResource {


    @GET
    @Path("thumbnail")
    public Response getThumbnail(@QueryParam("width") int width,
                                 @QueryParam("height") int height);


    /**
     *
     * @return the content of the blob as a {@code ByteSource}
     */
    ByteSource getContent();


    /**
     * Redirects the client to a temporary, signed URL via which the user can access this blob
     */
    @GET
    Response get();

}
