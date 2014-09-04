package org.activityinfo.service;

import com.google.gson.JsonParser;
import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.GcsBlobFieldStorageService;
import org.activityinfo.service.blob.UploadCredentials;

import javax.inject.Provider;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.IOException;

@Path("/service")
public class ServiceResources {

    public static final String GOOGLE_STORAGE_PREFIX = "/gs/";

    private final JsonParser jsonParser = new JsonParser();

    private final GcsBlobFieldStorageService blobFieldStorageService;
    private final Provider<AuthenticatedUser> authProvider;

    @Inject
    public ServiceResources(GcsBlobFieldStorageService blobFieldStorageService,
                            Provider<AuthenticatedUser> authProvider) {
        this.blobFieldStorageService = blobFieldStorageService;
        this.authProvider = authProvider;
    }


    @POST
    @Path("blob/{blobId}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getUploadCredentials(@PathParam("blobId") String blobId) throws IOException {

        return blobFieldStorageService.getUploadCredentials(authProvider.get(), new BlobId(blobId));
    }

}
