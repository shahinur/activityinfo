package org.activityinfo.service;

import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.blob.GcsBlobFieldStorageService;

import javax.inject.Provider;
import javax.ws.rs.Path;

@Path("/service")
public class ServiceResources {

    private final GcsBlobFieldStorageService blobFieldStorageService;
    private final Provider<AuthenticatedUser> authProvider;

    @Inject
    public ServiceResources(GcsBlobFieldStorageService blobFieldStorageService,
                            Provider<AuthenticatedUser> authProvider) {
        this.blobFieldStorageService = blobFieldStorageService;
        this.authProvider = authProvider;
    }

    @Path("blob")
    public GcsBlobFieldStorageService getBlobService() {
        return blobFieldStorageService;
    }


}
