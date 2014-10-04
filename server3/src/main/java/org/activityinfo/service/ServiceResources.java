package org.activityinfo.service;

import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.store.blob.GcsUserBlobService;

import javax.inject.Provider;
import javax.ws.rs.Path;

@Path("/service")
public class ServiceResources {

    private final GcsUserBlobService blobFieldStorageService;
    private final Provider<AuthenticatedUser> authProvider;

    @Inject
    public ServiceResources(GcsUserBlobService blobFieldStorageService,
                            Provider<AuthenticatedUser> authProvider) {
        this.blobFieldStorageService = blobFieldStorageService;
        this.authProvider = authProvider;
    }

    @Path("blob")
    public GcsUserBlobService getBlobService() {
        return blobFieldStorageService;
    }


}
