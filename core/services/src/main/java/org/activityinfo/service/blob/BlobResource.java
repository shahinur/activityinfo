package org.activityinfo.service.blob;

import org.activityinfo.model.auth.AuthenticatedUser;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * SubResource representing a single user blob.
 */
public final class BlobResource {

    private final AuthenticatedUser user;
    private final BlobId blobId;
    private final UserBlobService service;

    public BlobResource(UserBlobService service, AuthenticatedUser user, BlobId blobId) {
        this.service = service;
        this.user = user;
        this.blobId = blobId;
    }

    /**
     * @see org.activityinfo.service.blob.UserBlobService#getThumbnail(org.activityinfo.model.auth.AuthenticatedUser, BlobId, int, int)
     */
    @GET
    @Path("thumbnail")
    public Response getThumbnail(@QueryParam("width") int width,
                                 @QueryParam("height") int height) {

        return service.getThumbnail(user, blobId, width, height);
    }


    /**
     * @see org.activityinfo.service.blob.UserBlobService#serveBlob(org.activityinfo.model.auth.AuthenticatedUser, BlobId)
     */
    @GET
    Response serveBlob() {
        return service.serveBlob(user, blobId);
    }
}
