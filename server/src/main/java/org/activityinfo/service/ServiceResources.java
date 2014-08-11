package org.activityinfo.service;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import org.activityinfo.server.util.config.DeploymentConfiguration;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/service")
public class ServiceResources {

    public static final String GOOGLE_STORAGE_PREFIX = "/gs/";

    private final JsonParser jsonParser = new JsonParser();

    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private final DeploymentConfiguration config;

    @Inject
    public ServiceResources(DeploymentConfiguration config) {
        this.config = config;
    }



    @POST
    @Path("blob/{formClassId}/{fieldId}/{blobId}")
    public Response createUploadUrl(@PathParam("formClassId") String formClassId,
                                    @PathParam("fieldId") String fieldId,
                                    @PathParam("blobId") String blobId) throws IOException {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    }

}
